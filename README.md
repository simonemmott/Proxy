# FilesystemEntityManager 0.1.1
The file system entity manager provides a simplified API for persisting instances of objects on the file system as JSON or XML documents

The objects are serialized and deserialized using Gson and JAXB dependent on the configuration set for the class being persisted.

The FilesystemEntityManager also implements the `javax.persistence.EntityManager` interface and the FilesystemEntityManagerFactory implements the `javax.persistence.EntityManagerFactory` interface.

The file system entity manager uses the java FileLock API to manage concurrency issues over updating the persisted data of object instances
Locking of entities is handled in an optimistic write style. This behaviour cannot be configured and allows two entity managers to change the same object. The first one to `save(...)` the object will obtain a write lock on the object with the second entity managers call to `save(...)` throwing a `FemObjctLockedException`. The lock is released when the entity manager is committed or rolled back using the `commit()` or `rollback()` methods.

Changes made by the entity manger are initially written to a working directory specific to the entity manager that made the changes. This directory is
located outside the repository holding the entities persisted data. Changes are only made to the repository holding the entities persisted data when the 
entity manager is committed.

By default the entity manager persists data for a class of objects by wrapping them in a wrapper containing an Object Change Number (OCN). The OCN is automatically updated each time an entity is saved. The OCN for an entity when it is initially fetched into the entity manager is held in the entity manager and compared to the OCN currently held in the repository each time the entity is saved. If the OCN in the repository doesn't match the OCN held in the entity manager then the entity has been updated and committed in the repository by another entity manager since the entity was fetched into this entity manager and calls to `save(...)` and `delete(...)` will throw a `FemMutatedObjectException`. This prevents an object being updated or deleted based on data that is not the current data for the entity. Classes can be configured to not be persisted in a wrapper. In this case the entity manager will compute an MD5 hash of the file containing the entities persisted data and this MD5 hash is uses in lieu of the OCN.

The file system entity manager can persist entities in multiple locations (repositories) though each class is only persisted in a single repository. Each repository can hold persisted data for many classes with each class identifying a location within the repository to save its entities persisted data. 

Instances of `FilesystemEntityManager` are created via an instance of `FilesystemEntityManagerFactory` using its `entityManager()` method and instances of `FilesystemEntityManagerFactory` are created using the static `startup(...)` method of `FilesystemEntityManagerFactory`

File system entity managers provide the following methods for interacting with entities.

| Method                       | Description |
|------------------------------|-------------|
| `fetch(Class, Serializable)` |	This method fetches an instance of the given class for the given serializable key. The fetched object is cached in the entity manager allowing the same object be returned by subsequent calls to the same entity manager for the same class and key until the entity manager is rolled back when the entity managers cache is cleared |
| `save(Object)`               | This method saves the given object to the entity managers working directory ready to be committed to the repository. The persisted data in the cache of this object or a placeholder for a new entity is locked in the repository to prevent other entity managers from making changes to the same entity |
| `delete(Object)`             | This method flags the object as deleted in the entity manager. Objects can only be deleted after they have been fetched into the entity manager. Subsequent calls to fetch the same object will return a null value. The persited data in the repository is locked on the file system |
| `commit()`                   | This method commits all the changes to objects currently managed by this entity manager and updates the repository accordingly and realeases all locks held by the entity manager |
| `rollback()`                 | This method rolls back all changes to objects currently managed by this entity manager, clears this entity managers cache of attached objects and releases all the locks held by this entity manager |

For example output and detailed documentation please view the [javadoc](https://simonemmott.github.io/FilesystemEntityManager/index.html) documentation

### License

[GNU GENERAL PUBLIC LICENSE v3](http://fsf.org/)

## Basic Example
The java below
```
FilesystemEntityManagerFactory femf = FilesystemEntityManagerFactory.startup(new File("example/femf"));

femf.config().objectConfig(Too.class);
femf.config().setDefaultRepo(new File("example/repos/default"));

FilesystemEntityManager fem = femf.entityManager();

Too too = new Too()
		.setId("too")
		.setDescription("This is a Too!")
		.setSequence(1)
		.addBar(new Bar()
				.setId(1)
				.setName("Bar 1")
				.setDescription("This is bar one!"))
		.addBar(new Bar()
				.setId(2)
				.setName("Bar 2")
				.setDescription("This is bar two!"));
		
fem.save(too);

fem.commit();

fem.close();

femf.shutdown();
```
Creates the file too.json 
```json
{
  "obj": {
    "id": "too",
    "sequence": 1,
    "description": "This is a Too!",
    "bars": [
      {
        "id": 2,
        "name": "Bar 2",
        "description": "This is bar two!"
      },
      {
        "id": 1,
        "name": "Bar 1",
        "description": "This is bar one!"
      }
    ]
  },
  "ocn": 0
}
```
In the directory `example/repos/default/com/k2/FilesystemEntityManager/Too`

In order to do so the directory `example/repos` and `example/femf` must exist.

In the above example classes `Too` and `Bar` were as defined as below
```java
public class Too {

	@Expose public String id;
	@Expose public Integer sequence;
	@Expose public String description;
	@Expose public Set<Bar> bars;
	
	public String getId() { return id; }
	public Too setId(String id) {
		this.id = id;
		return this;
	}
	public Integer getSequence() {
		return sequence;
	}
	public Too setSequence(Integer sequence) {
		this.sequence = sequence;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public Too setDescription(String description) {
		this.description = description;
		return this;
	}
	public Too addBar(Bar bar) {
		if (bars == null) bars = new HashSet<Bar>();
		bars.add(bar);
		return this;
	}
	public Set<Bar> getBars() {
		return bars;
	}
	public Set<Bar> setBars(Set<Bar> bars) {
		this.bars = bars;
		return bars;
	}
	
}
```
```java
public class Bar implements Id<Bar, Integer> {

	@Expose Integer id;
	@Expose String name;
	@Expose String description;
	Foo foo;
	public Integer getId() { return id; }
	public Bar setId(Integer key) {
		id = key;
		return this;
	}
	public String getName() {
		return name;
	}
	public Bar setName(String name) {
		this.name = name;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public Bar setDescription(String description) {
		this.description = description;
		return this;
	}
}
```
The instance of the class `Too` can be retrieved with the java below
```java
Too too = fem.fetch(Too.class, "too");
```
Changes can be made to the fetched instance and saved with the save method
```java
fem.save(too);

fem.commit();
```
Having saved or fetched the instance `too` of the class `Too` the persisted data for the instance can be deleted with the following java
```java
fem.delete(too);

fem.commit();
```
Changes made to the persisted instance data can be discarded with the following java
```java
fem.rollback()
```
The `rollback()` method discards all changes made with the `save(...)` and `delete(...)` methods since the last call to the `commit()` method or since the entity manager was created.



## Getting Started

Download a jar file containing the latest version or fork this project and install in your IDE

Maven users can add this project using the following additions to the pom.xml file.
```maven
<dependencies>
    ...
    <dependency>
        <groupId>com.k2</groupId>
        <artifactId>FilesystemEntityManager</artifactId>
        <version>0.2.0</version>
    </dependency>
    ...
</dependencies>
```

## Working With FilesystemEntityManager

### Configuring Storage Parameters

The absolute persistence of the objects instance date is configured by configuring the FilesystemEntityManagerFactory that creates the entity manager.  Using a single entity manager factory to manage the repositories within a single application therefore ensures that all instances of a given class are persisted consistently.

The configuration of an instance of a FilesystemEntityManagerFactory is returned by calling the `config()` method of the FilesystemEntityManagerFactory.  When the entity manager factory is shutdown the current configuration of the entity manager factory is saved in the `femf.conf` file located in the manager directory specified when the instance of the entity manager factory is started up. When the entity manager factory is started up it reads the configuration from the `fem.conf` file in the manager directory. Consequently multiple JVMs can persist data in the same repositories using the same format for the persistent data.

The `fem.conf` file is in JSON format and can therefore be edited manually. Alternatively the configuration of an entity manager factory can be set systematically.

#### Systematic Configuration of Storage Parameters
Configuration that applies to all of the classes managed by file system entity managers are set by calling methods directly on the instance of `FemConfig` returned by the call to `config()` on the entity manager factory instance.

All these examples are based on an entity manager factory created with the following java
```java
femf = FilesystemEntityManagerFactory.startup(new File("example/new/femf"));
```

##### Global/Default Configuration
Configuration that can be applied to all classes managed by entity managers are as follows

| Method | Description |
|--------|-------------|
| `dataFormat(FemDataFormat)` | This method sets the default data format of classes persisted by entity managers created by this entity manager factory. The list of possible data formats is listed below. |
| `setRepo(String, File)`     | This method set the location of the repository identified by the String alias |
| `setDefaultRepo(File)`      | This method set the location of the default repository |

The examples below shows setting configuration that is applied to all classes managed by entity managers.

This example configures the default repository as `example/new/repos/default` and an additional repository at `example/new/repos/custom` that is identified with the alias `custom`
```java
femf.config()
	.setDefaultRepo(new File("example/new/repos/default"))
	.setRepo("custom", new File("example/new/repos/custom"));
```
This example configures the default data format as `XML`
```java
femf.config()
	.dataFormat(FemDataFormat.XML);
```

##### Class Specific Configuration
Each class managed by entity managers can define storage parameters specific to the class.

The entity manager factory instance defines a method to retrieve the storage configuation for a specific class `objectConfig(Class)`. Calling this method returns the current configuration for the given class and creates a new configuration for the given class if it does not exist. The method returns the instance of `FemObjectConfig` that holds the storage configuration for the given class.

The table below lists the parameters that can be set for the storage of a specific class.

| Method                            | Description |
|-----------------------------------|-------------|
| `dataFormat(FemDataFormat)`       | Set the format in which to store the persistence data for instances of the class |
| `dataStructure(FemDataStructure)` | Set the data structure in which the persistence data for instances of the class |
| `repository(String)`              | Identity the repository in which to store persistence data for instances of the class |
| `resourcePath(String)`            | Identify the location within the repository in which to store persistence data for instances of the class |

The following example sets the data format to `JSON` and the data structure to `OCN` for the class `Foo`.
```java
femf.config().objectConfig(Foo.class)
	.dataFormat(FemDataFormat.JSON)
	.dataStructure(FemDataStructure.OCN);
```
Note the above example is equivalent to:
```java
femf.config().objectConfig(Foo.class);
```
Since `JSON` and `OCN` are the default values for the data format and data structure respectively.

The following example sets the data format to `XML`, the data structure to `RAW`, the location within the repository to `"Too"` and to use the repository with the alias `"custom"` for the class `Too`.
```java
femf.config().objectConfig(Too.class)
	.dataFormat(FemDataFormat.XML)
	.dataStructure(FemDataStructure.RAW)
	.resourcePath(Too.class.getSimpleName())
	.repository("custom")
	.configure();
```
Note the use of the `configure()` method. This is required since the configuration has been changed.

##### Data Formats
Data formats define the syntax of the files used to store instance data.

| Data Format           | Description |
|-----------------------|-------------|
| `FemDataFormat.JSON ` | The instances persistent data is stored in Javascript Object Notation |
| `FemDataFormat.XML`   | The instances persistent data is stored in eXtensible Markup Language |

##### Data Structures
When persistent instance data is stored on the file system it can optionally be stored with an Object Change Number (OCN). The OCN is used to identify whether the persisted instance data has changed between it being fetched into the entity manager cache and being saved by the entity manager

| Data Structure      | Description |
|---------------------|-------------|
| `FemDataFormat.OCN` | The instances persistent data is stored wrapped in an object with an Integer OCN value |
| `FemDataFormat.RAW` | The instances persistent data is stored without an OCN value. In this case the files MD5 hash is used to identify whether the object has been changed beween being fetching into the entity manager cache and be saved by the entity manager |

### Writing Classes To Be Stored By Filesysytem Entity Managers

Classes can be written in either `JSON` or `XML` and are written by the Gson and JAXB API's respectively. Consequently the classes need o be annotated correctly with the `Gson` or `JAXB` annotations. In order to identity an instance of a class it must be possible to identify the primary key of the class. The instance data is stored in a file named `<key>.json` or `<key>.xml` where `<key>` is the string representation of the `Serializable` value returned by `IdenitityUtil.getId(...)` for the object. 

#### Writing Classes To Be Saved As JSON
Full details of how to write classes to be serialized using Gson can be found [here](https://github.com/google/gson/blob/master/UserGuide.md)

The example below shows a class to be serialised by `Gson`
```java
public class Foo implements Id<Foo, String> {
	
	@Expose String id;
	@Expose Integer sequence;
	@Expose String description;
	@Expose Set<Bar> bars;
	
	public String getId() { return id; }
	public Foo setId(Serializable key) {
		id = (String) key;
		return this;
	}
}
```
**Note** The above class implements the `Id` interface to allow `IdentityUtil` to extract the id of the classes instances. Alternatively the `javax.persistence.Id` annotation can be used instead or you can rely on `IdentiyUtil` to use reflection to identity a `Serializable` field named id.

##### Customizing The Gson Implementation

By default the Gson implementation used by the file system entity manager excludes fields without the `@Expose` annotation. The `Gson` implementation used by FilesystemEntityManager can be accessed through the `gson()` method of the entity manager factory.

e.g.
```java
Gson gson = femf.gson();
```
The `Gson` implementation used by the entity managers can be changed as shown below
```java
femf.gson(new GsonBuilder()
				.registerTypeAdapter(FemWrapper.class, new FemWrapperDeserializer(femf.localType()))
				.create());
```
**Note** If OCN wrappers are used to wrap objects with an Object Change Number (The default behaviour) the `Gson` implementation must be created with a type adapter to correctly deserialize the generic class `FemWrapper`. The method `localType()` of the entity manager factory returns a ThreadLocal<Type> variable that is populated with the expected type of the wrapped object for the thread that is deserializing the wrapper.

#### Writing Classes To Be Saved As XML
Full details of how to write classes to be serialized using JAXB can be found [here](https://docs.oracle.com/javase/8/docs/technotes/guides/xml/jaxb/index.html)

The example below shows a class to be serialised by `JAXB`
```java
@XmlRootElement(name = "foo")
public class XmlFoo implements Id<XmlFoo, String> {
	
	String id;
	Integer sequence;
	String description;
	@XmlElementWrapper(name="bars")
	@XmlElement(name="bar") Set<XmlBar> bars;
	
	public String getId() { return id; }
	public XmlFoo setId(Serializable key) {
		id = (String) key;
		return this;
	}
}
```
**Note** The above class implements the `Id` interface to allow `IdentityUtil` to extract the id of the classes instances. Alternatively the `javax.persistence.Id` annotation can be used instead or you can rely on `IdentiyUtil` to use reflection to identity a `Serializable` field named id.

### Fetching Instances Of Classes Managed By Filesystem Entity Managers

If a class has been configured to be managed by the entity managers then instances of the class can be fetched using the `fetch(Class, Serializable)` method of the entity manager.

The example below shows fetching an instance of the class `Foo` with the `String` id "foo".
```java
Foo foo = fem.fetch(Foo.class, "foo");
```
If there is no instance of the requested class with the requested id then a null value is returned. Once an instance has been fetched into an entity manager that instance of the class is held in the entity managers cache of attached objects and the same object will be returned by subsequent calls to `fetch(...)` for the same class and Serializable key without accessing the file system. Such an instance is considered to be attached to the entity manager. Objects in the entity managers cache will persist in the cache until the entity manager id closed or until the entity manager is rolled back using the `rollback()` method.

### Savings Instances Of Classes Managed By Filesystem Entity Managers

If a class has been configured to be managed by the entity managers then instances of the class can be saved using the `save(Object)` method of the entity manager.

The example below shows saving an instance of a managed class.
```java
Foo foo = new Foo();

foo.setId("newFoo");

foo = fem.save(foo);
```
An object that has been saved is held in the entity managers cache of attached objects and is considered to be attached to the entity manager.

If the saved object is not already attached to the entity manager then the object is treated as a new object and will be added to the repository in a new resource. If there is already an object in the repository with the same id value then a checked `FemDuplicateKeyException` is thrown by the call to `save(...)`.

If the saved object is already attached to the entity manager then the object is treated as an existing object and the resource for the object will be updated in the repository. If the object has changed and committed by another entity manager since it was attached to the entity manager that is saving it then a checked `FemMutatedObjectException` is thrown by the call to `save(...)`.

If the saved object is already attached to the entity manager but has been saved by another entity manager then a checked `FemObjectLockedException` is thrown by the call to `save(...)`.

Saving an object does not immediately update the repository. If the object is new then an empty resource is created in the repository and locked using the FileLock API to prevent other entity managers from saving an object with the same id. If the object already exists in the repository then the resource for the object is locked using the FileLock API to prevent other entity managers from saving changes to the object.

The saved object changes are applied to the repository when the `commit()` method of the entity manager is called.

If the changes are no longer required then the `rollback()` method of the entity manager can be called.

### Deleting Instances Of Classes Managed By Filesystem Entity Managers

If a class has been configured to be managed by the entity managers then instances of the class can be deleted using the `delete(Object)` method of the entity manager.

Before an object can be deleted via the entity manager it must first be attached to the entity manager either by fetching the object or saving a new object.

The example below shows deleting an instance of a managed class
```java
Foo foo = fem.fetch(Foo.class, "deleteMe");

fem.delete(foo);
```
If an attempt is made to delete an object that is not attached to the entity manager an unchecked `FemDetachedObjectError` is thrown.

If the object is being edited by another entity manager then a checked `FemObjectLockedException` is thrown.

Deleting an object does not immediately update the repository. The objects resource in the entity manager is locked using the FileLock API to prevent other entity managers from saving changes to the object.

The deleted objects are removed from the repository when the `commit()` method of the entity manager is called.

If the deletion is no longer required then the `rollback()` method of the entity manager can be called.

### Committing Changes Made By Filesystem Entity Managers

All changes to objects made by entity managers are cached until the `commit()` method of the entity manager is called. The changes are recorded in the working directory of the entity manager to minimise the risk of an IOException during commit and to minimise the time taken to commit changes.

When the entity manager commits its changes the saved version of object resources are moved from the entity managers working directory to the repository, resources for deleted entities are removed from the repository and file locks held by the entity manager are released.

After an entity manager has committed its changes the entity managers working directory will be empty as will the cache of changes held by the entity manager.  Consequently once a change has been committed in the entity manager the change cannot automatically be undone.

The example below shows committing changes made by an entity manager.
```java
FilesystemEntityManager fem = femf.entityManager();

Foo fetchedFoo = fem.fetch(Foo.class, "fetchedFoo");
fem.delete(fetchedFoo);

Foo newFoo = new Foo();
foo.setId("newFoo");
foo = fem.save(foo);

fem.commit();

fem.close();
```
The above example deletes the `Foo` "fetchedFoo" and saves the `Foo` "newFoo" and commits the changes to the repository

 
### Discarding Changes Made By Filesystem Entity Managers
The entity managers cache of changes can be discarded if they are not required. All changes held in the entity managers cache of changes are discarded and all locks held by the entity manager are released.

All objects attached to the entity manager are detached and all references to them should be discarded.

When an entity manager is closed by calling the `close()` method the entity manager is automatically rolled back and all uncommitted changes are discarded.

When the last reference to an entity manager is removed from the JVM the entity manager is automatically closed and changes managed by it are automatically rolled back.

When the entity manager factory is shutdown all open entity managers created by that entity manager factory are automatically closed, and their changes rolled back.

The example below shows rolling back changes made by an entity manager.
```java
FilesystemEntityManager fem = femf.entityManager();

Foo fetchedFoo = fem.fetch(Foo.class, "fetchedFoo");
fem.delete(fetchedFoo);

Foo newFoo = new Foo();
foo.setId("newFoo");
foo = fem.save(foo);

fem.rollback();

fem.close();
```
The above example makes no changes to the repository


