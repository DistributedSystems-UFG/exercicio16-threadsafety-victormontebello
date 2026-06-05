public class SynchronizedRGBTest {
    public static void main(String[] args) throws InterruptedException {
        SynchronizedRGB color = new SynchronizedRGB(255, 0, 0, "Red");

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                color.set(0, 255, 0, "Green");
                System.out.println(Thread.currentThread().getName()
                    + " set to Green");
                try { Thread.sleep(10); } catch (InterruptedException e) {}
            }
        }, "Writer-1");

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                int rgb = color.getRGB();
                String name = color.getName();
                System.out.println(Thread.currentThread().getName()
                    + " read: RGB=" + rgb + " Name=" + name
                    + " consistent=" + (name.equals("Red") || name.equals("Green")));
                try { Thread.sleep(10); } catch (InterruptedException e) {}
            }
        }, "Reader-1");

        t1.start(); t2.start();
        t1.join();  t2.join();

        // Demonstrating a TOCTOU race: getRGB then getName may be inconsistent
        SynchronizedRGB c2 = new SynchronizedRGB(10, 20, 30, "Original");
        Thread writer = new Thread(() -> { c2.set(100, 200, 250, "Changed"); }, "Writer");
        writer.start();
        // Reader reads rgb and name as separate calls — can see inconsistent state
        int r = c2.getRGB();
        // Between these two calls, the writer might change the color
        String n = c2.getName();
        System.out.println("Isolated read: RGB=" + r + " Name=" + n);
        writer.join();
    }
}
