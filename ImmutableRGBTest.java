public class ImmutableRGBTest {
    public static void main(String[] args) throws InterruptedException {
        // Start with a shared immutable reference
        ImmutableRGB color = new ImmutableRGB(255, 0, 0, "Red");

        Thread t1 = new Thread(() -> {
            ImmutableRGB local = color;
            for (int i = 0; i < 5; i++) {
                // invert() returns a NEW object, does not mutate the original
                local = local.invert();
                System.out.println(Thread.currentThread().getName()
                    + " -> " + local.getName() + " ("
                    + local.getRGB() + ")");
                try { Thread.sleep(10); } catch (InterruptedException e) {}
            }
        }, "Worker-1");

        Thread t2 = new Thread(() -> {
            ImmutableRGB local = color;
            for (int i = 0; i < 5; i++) {
                // Always reads a self-consistent snapshot
                int rgb = local.getRGB();
                String name = local.getName();
                System.out.println(Thread.currentThread().getName()
                    + " read: RGB=" + rgb + " Name=" + name
                    + " consistent=" + name.equals("Inverse of Red".substring(0, Math.min(name.length(), 14))));
                try { Thread.sleep(10); } catch (InterruptedException e) {}
            }
        }, "Reader-1");

        t1.start(); t2.start();
        t1.join();  t2.join();

        // ImmutableRGB is inherently consistent:
        // getRGB() and getName() are on the SAME object → always coherent
        ImmutableRGB c2 = new ImmutableRGB(10, 20, 30, "Original");
        ImmutableRGB c3 = c2.invert();
        System.out.println("c2: RGB=" + c2.getRGB() + " Name=" + c2.getName());
        System.out.println("c3: RGB=" + c3.getRGB() + " Name=" + c3.getName());
    }
}
