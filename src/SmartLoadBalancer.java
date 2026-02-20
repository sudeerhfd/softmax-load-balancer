import java.util.Random;

/**
 * DAĞITIK SİSTEMLER - FİNAL ÖDEVİ 1
 * Konu: Softmax Action Selection Load Balancer
 * Karşılaştırılan Algoritmalar: Random, Round-Robin, Softmax
 */
public class SmartLoadBalancer {

    // K adet sunucu (Cluster)
    static final int K = 3;
    // Tau (Sıcaklık): Keşif ve Sömürü dengesini ayarlar (Ödevdeki kritik parametre)
    static final double TAU = 1.5;
    static final int TOTAL_REQUESTS = 10000;
    static final Random rand = new Random();

    // Sunucuların gerçek (ama gizli) gecikme ortalamaları
    // Zamanla değişeceği için (Non-stationary) bu değerler simülasyonda güncellenir.
    static double[] serverCurrentMeans = {120.0, 75.0, 200.0};

    public static void main(String[] args) {
        System.out.println("=== Akıllı Yük Dengeleyici Simülasyonu Başladı ===");

        // 1. Rastgele Seçim Testi
        double randomAvg = runSimulation("RANDOM");
        // 2. Round-Robin Testi
        double rrAvg = runSimulation("ROUND_ROBIN");
        // 3. Softmax Testi (Senin Tasarladığın Çözüm)
        double softmaxAvg = runSimulation("SOFTMAX");

        System.out.println("\n------------------------------------------------");
        System.out.printf("Random Ortalama Gecikme       : %.2f ms\n", randomAvg);
        System.out.printf("Round-Robin Ortalama Gecikme  : %.2f ms\n", rrAvg);
        System.out.printf("SOFTMAX Ortalama Gecikme      : %.2f ms\n", softmaxAvg);
        System.out.println("------------------------------------------------");

        double gain = ((rrAvg - softmaxAvg) / rrAvg) * 100;
        System.out.printf("Analiz: Softmax, Round-Robin'e göre %%%.2f daha hızlı yanıt sağladı.\n", gain);
    }

    static double runSimulation(String algorithm) {
        // Her simülasyonda başlangıç koşullarını eşitle
        serverCurrentMeans = new double[]{120.0, 75.0, 200.0};

        double[] Q = new double[K]; // Sunucu performans tahminleri (Rewards)
        int[] N = new int[K];       // Seçilme sayıları
        double totalLatency = 0;
        int rrPointer = 0;

        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            int selected;

            // ADIM 1: SEÇİM MEKANİZMASI
            if (algorithm.equals("SOFTMAX")) {
                selected = softmaxSelect(Q);
            } else if (algorithm.equals("ROUND_ROBIN")) {
                selected = rrPointer;
                rrPointer = (rrPointer + 1) % K;
            } else {
                selected = rand.nextInt(K);
            }

            // ADIM 2: GECİKMEYİ SİMÜLE ET (Non-Stationary Environment)
            double latency = simulateServer(selected);
            totalLatency += latency;

            // ADIM 3: ÖĞRENME (Update)
            // Ödül = 1000 / Latency (Çünkü düşük gecikme = yüksek ödüldür)
            double reward = 1000.0 / latency;
            N[selected]++;
            // Hareketli Ortalama Formülü: Q_new = Q_old + (1/N) * (Reward - Q_old)
            Q[selected] += (reward - Q[selected]) / N[selected];
        }
        return totalLatency / TOTAL_REQUESTS;
    }

    static int softmaxSelect(double[] Q) {
        // --- NÜMERİK STABİLİTE PROBLEMİ ÇÖZÜMÜ ---
        // exp(x) işleminde x çok büyükse 'Overflow' (sonsuzluk) oluşur.
        // Çözüm: Her Q değerinden en büyük Q'yu çıkararak sayıları küçültüyoruz.
        double maxQ = Q[0];
        for (double q : Q) if (q > maxQ) maxQ = q;

        double[] expQ = new double[K];
        double sumExpQ = 0;

        for (int i = 0; i < K; i++) {
            // Nümerik stabilite: (Q[i] - maxQ)
            expQ[i] = Math.exp((Q[i] - maxQ) / TAU);
            sumExpQ += expQ[i];
        }

        // Olasılıksal Seçim (Roulette Wheel Selection)
        double r = rand.nextDouble();
        double cumulative = 0;
        for (int i = 0; i < K; i++) {
            cumulative += expQ[i] / sumExpQ;
            if (r <= cumulative) return i;
        }
        return K - 1;
    }

    static double simulateServer(int server) {
        // NON-STATIONARY: Sunucu performansı zamanla kayar (drift)
        serverCurrentMeans[server] += rand.nextGaussian() * 0.5;

        // Gürültülü ölçüm (Gaussian Noise)
        double noise = rand.nextGaussian() * 10.0;
        return Math.max(5.0, serverCurrentMeans[server] + noise);
    }
}