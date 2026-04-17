import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class exercise2_fractal_drawings extends JPanel {
    private BufferedImage fractalCanvas;
    private int currentBoxSize = 32;
    private double calculatedDimension = 0;

    // Variables to store the graph data calculated in calculate_dimension
    private ArrayList<Double> plotX = new ArrayList<>();
    private ArrayList<Double> plotY = new ArrayList<>();
    private double yIntercept = 0;

    public exercise2_fractal_drawings(String type) {
        // Increased width to 1000 to fit the graph on the right
        this.setPreferredSize(new Dimension(1050, 650));

        // 1. Pre-render the fractal to a BufferedImage
        fractalCanvas = new BufferedImage(600, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = fractalCanvas.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 600, 600);
        g2d.setColor(Color.BLUE);

        if (type.equals("Sierpinski")) {
            draw_sierpinski(g2d, 50, 550, 500, 6);
        } else {
            draw_tree(g2d, 300, 580, 150, Math.PI / 2, 9);
        }
        g2d.dispose();

        // 2. Initial Dimension Calculation (using a set of standard box sizes)
        int[] testSizes = {64, 32, 16, 8, 4};
        this.calculatedDimension = calculate_dimension(fractalCanvas, testSizes);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // ==========================================
        // LEFT SIDE: FRACTAL & DYNAMIC GRID
        // ==========================================
        g2.drawImage(fractalCanvas, 0, 0, null);

        g2.setColor(new Color(255, 0, 0, 100)); // Transparent red
        int boxesCounted = 0;

        for (int x = 0; x < fractalCanvas.getWidth(); x += currentBoxSize) {
            for (int y = 0; y < fractalCanvas.getHeight(); y += currentBoxSize) {
                if (boxContainsPixel(x, y, currentBoxSize)) {
                    g2.drawRect(x, y, currentBoxSize, currentBoxSize);
                    g2.fillRect(x, y, currentBoxSize, currentBoxSize);
                    boxesCounted++;
                }
            }
        }

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString("Dynamic Grid Box Size: " + currentBoxSize + "px", 20, 620);
        g2.drawString("Boxes Hit (N): " + boxesCounted, 240, 620);

        // ==========================================
        // RIGHT SIDE: LOG-LOG PLOT
        // ==========================================
        int ox = 650; // Origin X for graph
        int oy = 550; // Origin Y for graph

        // Draw Axes
        g2.setColor(Color.BLACK);
        g2.drawLine(ox, oy, ox + 350, oy); // X-axis
        g2.drawLine(ox, oy, ox, oy - 450); // Y-axis
        g2.drawString("log(1/size)", ox + 150, oy + 40);
        g2.drawString("log(count)", ox - 40, oy - 460);
        g2.drawString("Calculated Dimension (Slope): " + String.format("%.4f", calculatedDimension), ox + 20, oy - 480);

        // Scale factors to make the abstract numbers fit nicely on the screen
        double scaleX = 70; // Stretch X
        double scaleY = 40; // Stretch Y
        double shiftX = 5;  // Shift X to the right because log(1/size) is negative

        // Draw Line of Best Fit
        g2.setColor(new Color(200, 200, 200)); // Light Gray
        if (plotX.size() > 1) {
            double startX = plotX.get(0);
            double endX = plotX.get(plotX.size() - 1);

            double startY = calculatedDimension * startX + yIntercept;
            double endY = calculatedDimension * endX + yIntercept;

            int px1 = ox + (int)((startX + shiftX) * scaleX);
            int py1 = oy - (int)(startY * scaleY);
            int px2 = ox + (int)((endX + shiftX) * scaleX);
            int py2 = oy - (int)(endY * scaleY);

            g2.drawLine(px1, py1, px2, py2);
        }

        // Draw Data Points calculated from the testSizes array
        g2.setColor(Color.RED);
        for (int i = 0; i < plotX.size(); i++) {
            double xLog = plotX.get(i);
            double yLog = plotY.get(i);

            int px = ox + (int)((xLog + shiftX) * scaleX);
            int py = oy - (int)(yLog * scaleY);

            // Draw a small circle for the point
            g2.fill(new Ellipse2D.Double(px - 4, py - 4, 8, 8));

            // Label the point
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            g2.drawString(String.format("(%.2f, %.2f)", xLog, yLog), px + 8, py);
            g2.setColor(Color.RED);
        }
    }

    private boolean boxContainsPixel(int x, int y, int size) {
        for (int i = x; i < x + size && i < fractalCanvas.getWidth(); i++) {
            for (int j = y; j < y + size && j < fractalCanvas.getHeight(); j++) {
                if ((fractalCanvas.getRGB(i, j) & 0xFFFFFF) != 0xFFFFFF) {
                    return true;
                }
            }
        }
        return false;
    }

    // --- RECURSIVE ALGORITHMS ---
    private void draw_sierpinski(Graphics2D g, double x, double y, double size, int depth) {
        if (depth == 0) {
            int[] xp = {(int)x, (int)(x+size), (int)(x+size/2)};
            int[] yp = {(int)y, (int)y, (int)(y-(size*Math.sqrt(3)/2))};
            g.fillPolygon(xp, yp, 3);
        } else {
            double s = size/2; double h = s*Math.sqrt(3)/2;
            draw_sierpinski(g, x, y, s, depth-1);
            draw_sierpinski(g, x+s, y, s, depth-1);
            draw_sierpinski(g, x+s/2, y-h, s, depth-1);
        }
    }

    private void draw_tree(Graphics2D g, double x, double y, double len, double ang, int d) {
        double ex = x + len * Math.cos(ang);
        double ey = y - len * Math.sin(ang);
        g.drawLine((int)x, (int)y, (int)ex, (int)ey);
        if (d > 0) {
            draw_tree(g, ex, ey, len*0.75, ang + Math.PI/6, d-1);
            draw_tree(g, ex, ey, len*0.75, ang - Math.PI/6, d-1);
        }
    }

    // --- DIMENSION CALCULATION ---
    private double calculate_dimension(BufferedImage img, int[] sizes) {
        // Clear previous data if recalculating
        plotX.clear();
        plotY.clear();

        for (int s : sizes) {
            int count = 0;
            for (int x = 0; x < img.getWidth(); x += s) {
                for (int y = 0; y < img.getHeight(); y += s) {
                    if (boxContainsPixel(x, y, s)) count++;
                }
            }
            // Store points for graphing
            plotX.add(Math.log(1.0/s));
            plotY.add(Math.log(count));
        }

        // Linear regression slope
        double n = plotX.size(), sX = 0, sY = 0, sXY = 0, sX2 = 0;
        for (int i=0; i<n; i++) {
            sX += plotX.get(i);
            sY += plotY.get(i);
            sXY += plotX.get(i) * plotY.get(i);
            sX2 += plotX.get(i) * plotX.get(i);
        }

        double slope = (n*sXY - sX*sY) / (n*sX2 - sX*sX);

        // Calculate y-intercept for the line of best fit: b = (Σy - m * Σx) / n
        this.yIntercept = (sY - slope * sX) / n;

        return slope;
    }

    public void setBoxSize(int size) {
        this.currentBoxSize = size;
        repaint();
    }

    public static void main(String[] args) {
        // Run once for Sierpinski and once for Tree
        String[] types = {"Sierpinski", "Tree"};
        for (String type : types) {
            JFrame frame = new JFrame("Box Counting: " + type);
            exercise2_fractal_drawings panel = new exercise2_fractal_drawings(type);

            JSlider slider = new JSlider(2, 64, 32);
            slider.addChangeListener(e -> panel.setBoxSize(slider.getValue()));

            frame.setLayout(new BorderLayout());
            frame.add(panel, BorderLayout.CENTER);
            frame.add(slider, BorderLayout.SOUTH);
            frame.pack();
            frame.setVisible(true);
        }
    }
}