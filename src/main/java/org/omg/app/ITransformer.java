package org.omg.app;

import org.omg.gaia.Component;

/**
 * User: rye
 * Date: 12/30/14
 * Time: 15:29
 */
public interface ITransformer extends Component {

    public Packet<?> transform(Packet<?> packet);

}
