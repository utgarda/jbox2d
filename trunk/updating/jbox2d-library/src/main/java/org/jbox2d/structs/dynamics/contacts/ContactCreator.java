package org.jbox2d.structs.dynamics.contacts;

import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

// updated to rev 100
public interface ContactCreator {

	public Contact contactCreateFcn(Fixture fixtureA, Fixture fixtureB);
	
	public void contactDestroyFcn(Contact contact);
}