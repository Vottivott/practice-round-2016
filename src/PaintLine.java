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
        return "PAINT_LINE " + c1 + " " + r1 + " " + c2 + " " + r2;
    }

    @Override
    public void paint(PracticeProblem.State[][] wall) {
        if (r1 == r2) { //vertical
            for (int y = c1; y <= c2; y++) {
                wall[y][r1] = PracticeProblem.State.PAINTED;
            }
        } else {
            for (int x = r1; x <= r2; x++) {
                wall[c1][x] = PracticeProblem.State.PAINTED;
            }
        }
    }

    public int getLengthSquared() {
        return (r1-r2)*(r1-r2) + (c1-c2)*(c1-c2);
    }

    public boolean isVertical() {
        return r1 == r2;
    }

    @Override
    public int getX() {
        return r1;
    }

    @Override
    public int getY() {
        return c1;
    }

    @Override
    public int getS() {
        return 1;
    }

    public int emptySpots(PracticeProblem.State[][] picture) {

        int spots = 0;

        // Check if still possible
        if (this.isVertical()) {
            int x = this.getX();
            for (int y = this.getY(); y < Math.sqrt(this.getLengthSquared()); y++) {
                if (picture[y][x] != PracticeProblem.State.DO_PAINT) {
                    spots++;
                }
            }
        } else {
            int y = this.getY();
            for (int x = this.getX(); x < Math.sqrt(this.getLengthSquared()); x++) {
                if (picture[y][x] != PracticeProblem.State.DO_PAINT) {
                    spots++;
                }
            }
        }

        return spots;
    }

}
