public class EraseCell implements Command {
    int r;
    int c;

    public EraseCell(int r, int c) {
        this.r = r;
        this.c = c;
    }

    @Override
    public String toString() {
        return "ERASE_CELL " + r + " " + c;
    }

    @Override
    public void paint(boolean[][] wall) {
        wall[c][r] = false;
    }
}
