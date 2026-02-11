package com.codewithmad.smatool.source;

import com.codewithmad.smatool.model.Post;
import java.util.Arrays;
import java.util.List;

/**
 * Test için sahte veri kaynağı (DummyAPI)
 * 
 * NEDEN KULLANIYORUZ?
 * 1. Offline Test: İnternet bağlantısı gerektirmez
 * 2. Deterministik: Her seferinde aynı sonuç
 * 3. Hızlı: Network gecikmesi yok
 * 4. Geliştirme: API hazır olmadan özellik test edilebilir
 */
public class DummySource implements DataSource {
    
    @Override
    public List<Post> fetch() {
        return Arrays.asList(
            new Post("ahmet", "Ahmet Yılmaz", "Java programlama çok güzel ve öğrenmesi kolay"),
            new Post("ayse", "Ayşe Demir", "Nesne tabanlı programlama çok önemli bir konu"),
            new Post("mehmet", "Mehmet Kaya", "Spring Boot framework harika bir araç"),
            new Post("zeynep", "Zeynep Şahin", "Programlama öğrenmek zor ama güzel"),
            new Post("ali", "Ali Çelik", "Java ile backend geliştirme mükemmel"),
            new Post("fatma", "Fatma Aydın", "Singleton pattern kullanışlı bir tasarım deseni"),
            new Post("can", "Can Öztürk", "OOP kavramları başlangıçta karmaşık geliyor"),
            new Post("elif", "Elif Kara", "Java syntax temiz ve anlaşılır"),
            new Post("burak", "Burak Yıldız", "Kod yazmak bazen stresli ama eğlenceli"),
            new Post("selin", "Selin Arslan", "Programlama dünyası geniş ve heyecan verici")
        );
    }
}