public class PaintSquare implements Command {
    int r;
    int c;
    int s;

    public PaintSquare(int r, int c, int s) {
        this.r = r;
        this.c = c;
        this.s = s;
    }

    @Override
    public String toString() {
        return "PAINT_SQUARE " + c + " " + r + " " + s;
    }

    @Override
    public void paint(PracticeProblem.State[][] wall) {
        int startX = r - s;
        int startY = c - s;
        int side = 2*s + 1;
        for (int x = startX; x < startX+side; x++) {
            for (int y = startY; y < startY+side; y++) {
                wall[y][x] = PracticeProblem.State.PAINTED;
            }
        }
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
        return s;
    }
}
