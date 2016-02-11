public class PaintLine implements Command {
    int r1;
    int c1;
    int r2;
    int c2;

    public PaintLine(int r1, int c1, int r2, int c2) {
        assert r1 == r2 || c1 == c2;
        this.r1 = r1;
        this.c1 = c1;
        this.r2 = r2;
        this.c2 = c2;
    }

    @Override
    public String toString() {
        return "PAINT_LINE " + r1 + " " + c1 + " " + r2 + " " + c2;
    }

    @Override
    public void paint(boolean[][] wall) {
        if (r1 == r2) { //vertical
            for (int y = c1; y <= c2; y++) {
                wall[y][r1] = true;
            }
        } else {
            for (int x = c1; x <= c2; x++) {
                wall[c1][x] = true;
            }
        }
    }
}
