import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;

// Uçak sınıfı
class Ucak {
    String model, marka, seriNo;
    int koltukKapasitesi;
    boolean aktif;

    public Ucak(String model, String marka, String seriNo, int koltukKapasitesi, boolean aktif) {
        this.model = model;
        this.marka = marka;
        this.seriNo = seriNo;
        this.koltukKapasitesi = koltukKapasitesi;
        this.aktif = aktif;
    }

    @Override
    public String toString() {
        return String.format("%s %s (SN:%s) - Kapasite: %d %s",
                marka, model, seriNo, koltukKapasitesi, aktif ? "(Aktif)" : "(Pasif)");
    }
}

// Lokasyon sınıfı
class Lokasyon {
    String ulke, sehir, havaalani;
    boolean aktif;

    public Lokasyon(String ulke, String sehir, String havaalani, boolean aktif) {
        this.ulke = ulke;
        this.sehir = sehir;
        this.havaalani = havaalani;
        this.aktif = aktif;
    }

    @Override
    public String toString() {
        return String.format("%s (%s, %s) %s",
                havaalani, sehir, ulke, aktif ? "Aktif" : "Pasif");
    }
}

// Uçuş sınıfı
class Ucus {
    String id;
    Lokasyon kalkis, varis;
    LocalDateTime tarihSaat;
    Ucak ucak;
    double temelFiyat;
    int dolu;

    public Ucus(String id, Lokasyon kalkis, Lokasyon varis, LocalDateTime tarihSaat, Ucak ucak, double temelFiyat) {
        this.id = id;
        this.kalkis = kalkis;
        this.varis = varis;
        this.tarihSaat = tarihSaat;
        this.ucak = ucak;
        this.temelFiyat = temelFiyat;
        this.dolu = 0;
    }

    public boolean bosYerVarMi(int adet) {
        return dolu + adet <= ucak.koltukKapasitesi;
    }

    public void yerAyir(int adet) {
        dolu += adet;
    }

    @Override
    public String toString() {
        return String.format("%s: %s -> %s %s | Fiyat: %.2f TL | Boş Koltuk: %d",
                id, kalkis, varis, tarihSaat, temelFiyat, ucak.koltukKapasitesi - dolu);
    }
}

// Rezervasyon sınıfı
class Rezervasyon {
    String no;
    Ucus ucus;
    String ad, soyad, cinsiyet;
    int yas;
    double odeme;

    public Rezervasyon(String no, Ucus ucus, String ad, String soyad, int yas, double odeme, String cinsiyet) {
        this.no = no;
        this.ucus = ucus;
        this.ad = ad;
        this.soyad = soyad;
        this.yas = yas;
        this.odeme = odeme;
        this.cinsiyet = cinsiyet;
    }

    @Override
    public String toString() {
        return String.format("%s: %s %s (%d yaş, %s) | Uçuş: %s | Ödeme: %.2f TL",
                no, ad, soyad, yas, cinsiyet, ucus.id, odeme);
    }
}

// Ana uygulama sınıfı
public class Uygulama {
    static List<Ucak> ucaklar = new ArrayList<>();
    static List<Lokasyon> lokasyonlar = new ArrayList<>();
    static List<Ucus> ucuslar = new ArrayList<>();
    static List<Rezervasyon> rezervasyonlar = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        verileriHazirla();
        menu();
    }

    // Hazır veriler
    static void verileriHazirla() {
        ucaklar.add(new Ucak("A320", "Airbus", "SN1", 150, true));
        ucaklar.add(new Ucak("737", "Boeing", "SN2", 160, true));

        lokasyonlar.add(new Lokasyon("Türkiye", "İstanbul", "IST", true));
        lokasyonlar.add(new Lokasyon("Türkiye", "Ankara", "ESB", true));
        lokasyonlar.add(new Lokasyon("Almanya", "Berlin", "BER", true));

        LocalDateTime baslangicZamani = LocalDateTime.now().plusDays(1);
        Random r = new Random();

        for (int i = 1; i <= 25; i++) {
            Lokasyon kalkis = lokasyonlar.get(r.nextInt(lokasyonlar.size()));
            Lokasyon varis;
            do {
                varis = lokasyonlar.get(r.nextInt(lokasyonlar.size()));
            } while (varis == kalkis);

            Ucak ucak = ucaklar.get(r.nextInt(ucaklar.size()));
            double fiyat = 500 + r.nextInt(501);

            ucuslar.add(new Ucus("UC" + i, kalkis, varis, baslangicZamani.plusHours(i), ucak, fiyat));
        }
    }

    // Menü
    static void menu() {
        while (true) {
            System.out.println("\n--- Uçak Bilet Rezervasyon Sistemi ---");
            System.out.println("1 - Uçuşları Listele");
            System.out.println("2 - Rezervasyon Yap");
            System.out.println("3 - Rezervasyonları Görüntüle");
            System.out.println("0 - Çıkış");
            System.out.print("Seçiminiz: ");
            String secim = scanner.nextLine();

            switch (secim) {
                case "1":
                    ucuslar.forEach(System.out::println);
                    break;
                case "2":
                    rezervasyonYap();
                    break;
                case "3":
                    rezervasyonlar.forEach(System.out::println);
                    break;
                case "0":
                    System.out.println("Çıkılıyor...");
                    return;
                default:
                    System.out.println("Geçersiz seçim. Tekrar deneyin.");
            }
        }
    }

    // Rezervasyon işlemi
    static void rezervasyonYap() {
        try {
            System.out.print("Kaç bilet almak istiyorsunuz? ");
            int adet = Integer.parseInt(scanner.nextLine());

            System.out.print("Uçuş ID: ");
            String ucusID = scanner.nextLine().trim().toUpperCase();

            Optional<Ucus> secilenUcus = ucuslar.stream()
                    .filter(u -> u.id.equalsIgnoreCase(ucusID))
                    .findFirst();

            if (secilenUcus.isEmpty() || !secilenUcus.get().bosYerVarMi(adet)) {
                System.out.println("Uçuş bulunamadı veya yeterli boş yer yok.");
                return;
            }

            Ucus ucus = secilenUcus.get();
            ucus.yerAyir(adet);
            double toplamOdeme = 0;

            for (int i = 1; i <= adet; i++) {
                System.out.println(i + ". yolcu bilgileri:");
                System.out.print("  Ad: ");
                String ad = scanner.nextLine();
                System.out.print("  Soyad: ");
                String soyad = scanner.nextLine();
                System.out.print("  Cinsiyet: ");
                String cinsiyet = scanner.nextLine();
                System.out.print("  Doğum Tarihi (YYYY-MM-DD): ");
                LocalDate dogumTarihi = LocalDate.parse(scanner.nextLine());

                int yas = Period.between(dogumTarihi, ucus.tarihSaat.toLocalDate()).getYears();
                double ucret = ucus.temelFiyat;
                if (yas >= 2 && yas < 12) {
                    ucret *= 0.5; // çocuk indirimi
                }

                String rezervasyonNo = "RZ" + (rezervasyonlar.size() + 1);
                rezervasyonlar.add(new Rezervasyon(rezervasyonNo, ucus, ad, soyad, yas, ucret, cinsiyet));
                toplamOdeme += ucret;
            }

            System.out.printf("Toplam ödeme: %.2f TL%n", toplamOdeme);

        } catch (Exception e) {
            System.out.println("Hata oluştu. Lütfen bilgileri doğru formatta giriniz.");
        }
    }
}