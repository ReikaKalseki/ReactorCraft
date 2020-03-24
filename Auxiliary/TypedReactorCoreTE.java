package Reika.ReactorCraft.Auxiliary;

import Reika.ReactorCraft.Registry.ReactorType;

public interface TypedReactorCoreTE extends Temperatured, ReactorCoreTE {

	public ReactorType getReactorType();

}
