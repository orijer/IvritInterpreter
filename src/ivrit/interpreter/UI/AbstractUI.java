package ivrit.interpreter.UI;

import ivrit.interpreter.UserIO.IvritIO;

/**
 * An abstract UI that the actuall UI objects should extend.
 * Every UI object should declare its IO object.
 */
public abstract class AbstractUI implements UI {
    protected IvritIO io;

    public AbstractUI(IvritIO io) {
        this.io = io;
    }
}
