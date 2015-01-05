package de.mhus.osgi.tutorial.websampleapp;


public class WSEntity {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean equals(Object o) {
		if (name != null && o != null && o instanceof WSEntity)
			return name.equals( ((WSEntity)o).getName() );
		return super.equals(o);
	}
}
