/**
 * This file is part of the Joana IFC project. It is developed at the
 * Programming Paradigms Group of the Karlsruhe Institute of Technology.
 *
 * For further details on licensing please read the information at
 * http://joana.ipd.kit.edu or contact the authors.
 */
package edu.kit.joana.wala.core;

import edu.kit.joana.wala.core.params.objgraph.ModRefFieldCandidate;
import gnu.trove.map.TIntObjectMap;

/**
 * @author Martin Mohr &lt;martin.mohr@kit.edu&gt;
 */
public interface ParameterPointsToConsumer {

	/**
	 * @param pdgnode2modref
	 */
	void commitPDGNode2ModRefMapping(TIntObjectMap<ModRefFieldCandidate> pdgnode2modref);

}
