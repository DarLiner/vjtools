package com.vip.vjtools.vjmap.oops;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import sun.jvm.hotspot.debugger.AddressException;
import sun.jvm.hotspot.memory.SystemDictionary;
import sun.jvm.hotspot.oops.InstanceKlass;
import sun.jvm.hotspot.oops.Klass;
import sun.jvm.hotspot.oops.Oop;
import sun.jvm.hotspot.oops.OopField;
import sun.jvm.hotspot.oops.OopUtilities;
import sun.jvm.hotspot.runtime.VM;

public class LoadedClassAccessor {

	public void pringLoadedClass() {
		try {
			System.err.println("Finding classes in System Dictionary..");
			final ArrayList<InstanceKlass> klasses = new ArrayList<>(128);

			SystemDictionary dict = VM.getVM().getSystemDictionary();
			dict.classesDo(new SystemDictionary.ClassVisitor() {
				public void visit(Klass k) {
					if (k instanceof InstanceKlass) {
						klasses.add((InstanceKlass) k);
					}
				}
			});

			Collections.sort(klasses, new Comparator<InstanceKlass>() {
				public int compare(InstanceKlass x, InstanceKlass y) {
					return x.getName().asString().compareTo(y.getName().asString());
				}
			});

			System.out.println("#class             #loader");
			System.out.println("-----------------------------------------------");
			for (InstanceKlass k : klasses) {
				System.out.printf("%s, %s\n", getClassNameFrom(k), getClassLoaderOopFrom(k));
			}
		} catch (AddressException e) {
			System.err.println("Error accessing address 0x" + Long.toHexString(e.getAddress()));
			e.printStackTrace();
		}
	}

	private static String getClassLoaderOopFrom(InstanceKlass klass) {
		Oop loader = klass.getClassLoader();
		return loader != null ? getClassNameFrom((InstanceKlass) loader.getKlass()) + " @ " + loader.getHandle()
				: "<bootstrap>";
	}

	private static String getClassNameFrom(InstanceKlass klass) {
		return klass != null ? klass.getName().asString().replace('/', '.') : null;
	}
}
