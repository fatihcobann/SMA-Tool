package com.codewithmad.smatool.analyzer;

import com.codewithmad.smatool.model.Post;
import com.codewithmad.smatool.source.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Thread-safe Singleton Pattern ile sosyal medya analizci
 */
public class SocialMediaAnalyzer {
    
    private static volatile SocialMediaAnalyzer instance;
    private final List<Post> posts;
    private final Locale TR = new Locale("tr", "TR");
    
    private final Set<String> stopWords = new HashSet<>(Arrays.asList(
        "ve", "veya", "ama", "fakat", "ise", "de", "da", "bu", "şu", "o", 
        "ile", "için", "gibi", "çok", "bir", "en", "ki", "ya", "hem", 
        "ben", "sen", "mi", "mı", "mu", "mü", "olan", "daha", "sonra"
    ));

    private SocialMediaAnalyzer() {
        this.posts = Collections.synchronizedList(new ArrayList<>());
    }

    public static SocialMediaAnalyzer getInstance() {
        if (instance == null) {
            synchronized (SocialMediaAnalyzer.class) {
                if (instance == null) {
                    instance = new SocialMediaAnalyzer();
                }
            }
        }
        return instance;
    }

    public void loadFromSource(DataSource source) throws IOException {
        List<Post> newPosts = source.fetch();
        if (newPosts.isEmpty()) {
            throw new IOException("Hiç gönderi bulunamadı!");
        }
        posts.addAll(newPosts);
    }

    public int getTotalPosts() {
        return posts.size();
    }

    private String[] tokenize(String text) {
        String cleaned = text.toLowerCase(TR).replaceAll("[^a-zçğıöşü\\s]", " ");
        return cleaned.trim().split("\\s+");
    }

    public Map<String, Integer> analyzeKeywords() {
        Map<String, Integer> wordCount = new HashMap<>();
        
        synchronized (posts) {
            for (Post post : posts) {
                for (String word : tokenize(post.getMetin())) {
                    if (word.length() > 2 && !stopWords.contains(word)) {
                        wordCount.merge(word, 1, Integer::sum);
                    }
                }
            }
        }
        return wordCount;
    }

    public void printKeywordAnalysis() {
        Map<String, Integer> keywords = analyzeKeywords();
        
        List<Map.Entry<String, Integer>> sorted = keywords.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(10)
            .collect(Collectors.toList());

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║    KEYWORD ANALİZİ (TOP 10)           ║");
        System.out.println("╚════════════════════════════════════════╝");

        int rank = 1;
        for (Map.Entry<String, Integer> entry : sorted) {
            String bar = "█".repeat(Math.min(entry.getValue(), 20));
            System.out.printf("%2d. %-15s │ %3d kez │ %s\n", 
                rank++, entry.getKey(), entry.getValue(), bar);
        }
    }

    public void printSentimentAnalysis() {
        Set<String> positive = new HashSet<>(Arrays.asList(
            "güzel", "harika", "mükemmel", "iyi", "kolay", "önemli", "eğlenceli", "temiz", "anlaşılır",
            "başarılı", "keyifli", "mutlu", "etkileyici", "yararlı", "verimli", "olumlu", "sevimli", "hoş", 
            "tatlı", "ilginç", "sade", "rahat", "doğru", "yaratıcı", "akıcı", "dengeli", "güvenilir", 
            "hızlı", "profesyonel", "kaliteli", "gelişmiş", "yenilikçi", "net", "kapsamlı", "dikkatli", 
            "motive edici", "tatmin edici", "memnun", "başarılı", "parlak", "pozitif", "etkili", "faydalı",
            "hoşnut", "anlamlı", "aydınlık", "sezgisel", "kullanışlı", "uyumlu", "kararlı", "nazik", "güçlü"
        ));

        
        Set<String> negative = new HashSet<>(Arrays.asList(
            "kötü", "berbat", "zor", "karmaşık", "stresli",
            "anlaşılmaz", "bozuk", "eksik", "hatalı", "yavaş", "sıkıcı", "sinir bozucu", 
            "verimsiz", "negatif", "bunaltıcı", "karmakarışık", "çirkin", "yetersiz", 
            "zayıf", "rahatsız edici", "yorgun", "umutsuz", "bıktırıcı", "kısıtlı", "boş", 
            "kırıcı", "güvensiz", "gereksiz", "fazla", "kararsız", "haksız", "ilgisiz", 
            "dengesiz", "moral bozucu", "karmaşık", "zorlama", "yetersiz", "rahatsız", 
            "pişman", "değersiz", "karanlık", "uygunsuz", "vasat", "beceriksiz", "zedeleyici"
        ));

        int pos = 0, neg = 0, neutral = 0;

        synchronized (posts) {
            for (Post post : posts) {
                String text = post.getMetin().toLowerCase(TR);
                boolean hasPositive = positive.stream().anyMatch(text::contains);
                boolean hasNegative = negative.stream().anyMatch(text::contains);

                if (hasPositive) pos++;
                else if (hasNegative) neg++;
                else neutral++;
            }
        }

        int total = posts.size();
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║    DUYGU ANALİZİ (SENTIMENT)          ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.printf("😊 Pozitif: %3d (%%%d)\n", pos, (pos * 100 / total));
        System.out.printf("😞 Negatif: %3d (%%%d)\n", neg, (neg * 100 / total));
        System.out.printf("😐 Nötr   : %3d (%%%d)\n", neutral, (neutral * 100 / total));
    }

    public void printActiveUsers(int limit) {
        Map<String, Integer> userCount = new HashMap<>();
        
        synchronized (posts) {
            for (Post post : posts) {
                userCount.merge(post.getKullanici(), 1, Integer::sum);
            }
        }

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║    EN AKTİF KULLANICILAR              ║");
        System.out.println("╚════════════════════════════════════════╝");

        userCount.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(limit)
            .forEach(entry -> {
                String bar = "▓".repeat(entry.getValue());
                System.out.printf("👤 %-12s │ %2d gönderi │ %s\n", 
                    entry.getKey(), entry.getValue(), bar);
            });
    }

    public void printStatistics() {
        int totalWords = 0, minWords = Integer.MAX_VALUE, maxWords = 0;

        synchronized (posts) {
            for (Post post : posts) {
                int wordCount = tokenize(post.getMetin()).length;
                totalWords += wordCount;
                minWords = Math.min(minWords, wordCount);
                maxWords = Math.max(maxWords, wordCount);
            }
        }

        long uniqueUsers = posts.stream().map(Post::getKullanici).distinct().count();

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║    İSTATİSTİKSEL ÖZET                 ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("📊 Toplam gönderi      : " + posts.size());
        System.out.println("👥 Benzersiz kullanıcı : " + uniqueUsers);
        System.out.println("📝 Toplam kelime       : " + totalWords);
        System.out.println("📈 Ortalama kelime     : " + (totalWords / posts.size()));
        System.out.println("📉 En kısa gönderi     : " + minWords + " kelime");
        System.out.println("📊 En uzun gönderi     : " + maxWords + " kelime");
    }

    public void saveReport(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(filename, StandardCharsets.UTF_8)) {
            writer.println("═══════════════════════════════════════════════════");
            writer.println("        SOSYAL MEDYA ANALİZ RAPORU");
            writer.println("═══════════════════════════════════════════════════");
            writer.println("Tarih: " + new Date());
            writer.println("Toplam Gönderi: " + posts.size());
            writer.println("\n--- EN POPÜLER KELİMELER (TOP 10) ---");
            
            Map<String, Integer> keywords = analyzeKeywords();
            keywords.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .forEach(e -> writer.println(e.getKey() + ": " + e.getValue()));
            
            writer.println("\n═══════════════════════════════════════════════════");
        }
        System.out.println("\n✅ Rapor kaydedildi: " + filename);
    }
}