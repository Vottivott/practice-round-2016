
public interface Command {

    int getX();
    int getY();
    int getS();

    abstract void paint(PracticeProblem.State[][] wall);
}
