package example.proxy;

public class Bar extends Foo {

	public Bar() {}
	public Bar(Long id) { super(id); }
	
	private String message;

	@Override
	public void setDescription(String desciption) {
		super.setDescription(desciption+" From Bar");
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
