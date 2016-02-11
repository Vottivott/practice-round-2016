public class EraseCell implements Command {
    int r;
    int c;

    public EraseCell(int r, int c) {
        this.r = r;
        this.c = c;
    }

    @Override
    public String toString() {
        return "ERASE_CELL " + c + " " + r;
    }

    @Override
    public void paint(PracticeProblem.State[][] wall) {
        wall[c][r] = PracticeProblem.State.DONT_PAINT;
    }

    @Override
    public int getX() {
        return r;
    }

    @Override
    public int getY() {
        return c;
    }

    @Override
    public int getS() {
        return 1;
    }
}
