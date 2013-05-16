/**
 * This file is part of the Joana IFC project. It is developed at the
 * Programming Paradigms Group of the Karlsruhe Institute of Technology.
 *
 * For further details on licensing please read the information at
 * http://joana.ipd.kit.edu or contact the authors.
 */
package edu.kit.joana.api.lattice;

import edu.kit.joana.ifc.sdg.lattice.IEditableLattice;
import edu.kit.joana.ifc.sdg.lattice.IStaticLattice;
import edu.kit.joana.ifc.sdg.lattice.impl.EditableLatticeSimple;

/**
 * Utility class providing standard security levels and standard lattices.
 * 
 * @author Martin Mohr
 */
public final class BuiltinLattices {

	/**
	 * standard security level 'low' - used as bottom element in all lattices
	 * provided by this class
	 */
	public static final String STD_SECLEVEL_LOW = "low";

	/**
	 * standard security level 'high' - used as top element in all lattices
	 * provided by this class
	 */
	public static final String STD_SECLEVEL_HIGH = "high";

	/**
	 * standard security level 'mid' - used in {@link #getTernaryLattice
	 * ternary lattice}
	 */
	public static final String STD_SECLEVEL_MID = "mid";

	/**
	 * standard security level 'midA' - used in {@link #getDiamondLattice() the
	 * standard diamond lattice}
	 */
	public static final String STD_SECLEVEL_DIAMOND_A = "midA";

	/**
	 * standard security level 'midB' - used in {@link #getDiamondLattice() the
	 * standard diamond lattice}
	 */
	public static final String STD_SECLEVEL_DIAMOND_B = "midB";

	/**
	 * standard binary lattice, consisting of levels 'low' and 'high', generated
	 * by relation 'low <= high'
	 */
	private static IStaticLattice<String> binary = null;

	/**
	 * standard ternary lattice, consisting of levels 'low', 'mid' and 'high',
	 * generated by relations 'low <= mid' and 'mid <= high'
	 */
	private static IStaticLattice<String> ternary = null;

	/**
	 * standard diamond lattice, , consisting of levels 'low', 'midA', 'midB'
	 * and 'high', generated by relations 'low <= midA', 'low <= midB', 'midA <=
	 * high', 'midB <= high'
	 */
	private static IStaticLattice<String> diamond = null;

	/**
	 * This is a utility class - so make instantiation impossible!
	 */
	private BuiltinLattices() {

	}

	/**
	 * Factory method for the standard binary lattice consisting of the two
	 * elements {@link BuiltinLattices#STD_SECLEVEL_LOW "low"} and
	 * {@link BuiltinLattices#STD_SECLEVEL_HIGH high} which satisfy the relation
	 * low <= high.
	 * 
	 * @return standard lattice consisting of the two elements
	 *         {@link BuiltinLattices#STD_SECLEVEL_LOW "low"} and
	 *         {@link BuiltinLattices#STD_SECLEVEL_HIGH high}
	 */
	public static final IStaticLattice<String> getBinaryLattice() {
		if (binary == null) {
			IEditableLattice<String> retLattice = new EditableLatticeSimple<String>();
			retLattice.addElement(BuiltinLattices.STD_SECLEVEL_LOW);
			retLattice.addElement(BuiltinLattices.STD_SECLEVEL_HIGH);
			retLattice.setImmediatelyGreater(BuiltinLattices.STD_SECLEVEL_LOW, BuiltinLattices.STD_SECLEVEL_HIGH);
			binary = retLattice;
		}
		return binary;
	}

	/**
	 * Factory method for the standard ternary lattice consisting of the three
	 * elements {@link #STD_SECLEVEL_LOW "low"}, {@link #STD_SECLEVEL_MID 'mid'}
	 * and {@link #STD_SECLEVEL_HIGH high}, generated by the relations low <=
	 * mid and mid <= high.
	 * 
	 * @return ternary lattice consisting of the three elements
	 *         {@link #STD_SECLEVEL_LOW "low"}, {@link #STD_SECLEVEL_MID 'mid'}
	 *         and {@link #STD_SECLEVEL_HIGH high}, generated by the relations
	 *         low <= mid and mid <= high
	 */
	public static final IStaticLattice<String> getTernaryLattice() {
		if (ternary == null) {
			IEditableLattice<String> retLattice = new EditableLatticeSimple<String>();
			retLattice.addElement(BuiltinLattices.STD_SECLEVEL_LOW);
			retLattice.addElement(BuiltinLattices.STD_SECLEVEL_MID);
			retLattice.addElement(BuiltinLattices.STD_SECLEVEL_HIGH);
			retLattice.setImmediatelyGreater(BuiltinLattices.STD_SECLEVEL_LOW, BuiltinLattices.STD_SECLEVEL_MID);
			retLattice.setImmediatelyGreater(BuiltinLattices.STD_SECLEVEL_MID, BuiltinLattices.STD_SECLEVEL_HIGH);
			ternary = retLattice;
		}
		return ternary;
	}

	/**
	 * Factory method for the standard diamond lattice consisting of the four
	 * elements {@link #STD_SECLEVEL_LOW "low"}, {@link #STD_SECLEVEL_DIAMOND_A
	 * "midA"}, {@link #STD_SECLEVEL_DIAMOND_B "midB"} and
	 * {@link #STD_SECLEVEL_HIGH "high"}, generated by the relations low <=
	 * midA, low <= midB, midA <= high, midB <= high. Note that midA and midB
	 * are incomparable in this lattice.
	 * 
	 * @return standard diamond lattice consisting of the four elements
	 *         {@link #STD_SECLEVEL_LOW "low"}, {@link #STD_SECLEVEL_DIAMOND_A 
	 *         "midA'}, {@link #STD_SECLEVEL_DIAMOND_A "midB"} and
	 *         {@link #STD_SECLEVEL_HIGH "high"}, generated by the relations low
	 *         <= midA, low <= midB, midA <= high, midB <= high
	 */
	public static final IStaticLattice<String> getDiamondLattice() {
		if (diamond == null) {
			IEditableLattice<String> retLattice = new EditableLatticeSimple<String>();
			retLattice.addElement(BuiltinLattices.STD_SECLEVEL_LOW);
			retLattice.addElement(BuiltinLattices.STD_SECLEVEL_DIAMOND_A);
			retLattice.addElement(BuiltinLattices.STD_SECLEVEL_DIAMOND_B);
			retLattice.addElement(BuiltinLattices.STD_SECLEVEL_HIGH);
			retLattice.setImmediatelyGreater(BuiltinLattices.STD_SECLEVEL_LOW, BuiltinLattices.STD_SECLEVEL_DIAMOND_A);
			retLattice.setImmediatelyGreater(BuiltinLattices.STD_SECLEVEL_LOW, BuiltinLattices.STD_SECLEVEL_DIAMOND_B);
			retLattice.setImmediatelyGreater(BuiltinLattices.STD_SECLEVEL_DIAMOND_A, BuiltinLattices.STD_SECLEVEL_HIGH);
			retLattice.setImmediatelyGreater(BuiltinLattices.STD_SECLEVEL_DIAMOND_B, BuiltinLattices.STD_SECLEVEL_HIGH);
			diamond = retLattice;
		}
		return diamond;
	}
	

}
