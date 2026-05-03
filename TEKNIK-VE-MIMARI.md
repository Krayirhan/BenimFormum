# Benim Formum — Teknik mimari ve kod yapısı

Bu belge **pazarlama dışı**, ürünün yazılım mimarisi, teknoloji yığını ve kod organizasyonunu özetler. Hedef okuyucu: geliştirici veya teknik inceleyen.

---

## 1. Mimari ilke

Proje **Clean Architecture + MVVM** ile kurgulanır.

**Veri akışı (özet):**

`UI (Compose)` → `ViewModel` → `UseCase` → `Repository arayüzü (domain)` → `Repository uygulaması (data)` → `DAO` → `Room`

**Katı kurallar (projede benimsenen):**

- Compose UI, DAO’ya doğrudan erişmez.  
- ViewModel, DAO’ya doğrudan erişmez.  
- ViewModel dışa **değişmez `StateFlow`** ile durum yayınlar.  
- İş kuralları **UseCase** içinde toplanır.  
- Room **tek yerel kaynak** (source of truth) kabul edilir.

---

## 2. Modül ve dizin yapısı (özet)

Çalışma kökü: `android-app/` (Android Gradle projesi).

| Alan | Rol |
|------|-----|
| `app/src/main/java/.../feature/*` | Ekran + ViewModel (MVVM sunumu) |
| `app/src/main/java/.../domain/*` | Varlıklar, repository sözleşmeleri, use case’ler |
| `app/src/main/java/.../data/*` | Repository uygulamaları, Room, DataStore |
| `app/src/main/java/.../core/*` | Ortak UI bileşenleri, yardımcılar |
| `app/src/main/java/.../navigation/*` | `NavHost`, sekmeler, yükleme kabuğu |
| `app/src/main/java/.../ui/theme/*` | Material 3 tema, özel renk şeması, tipografi |
| `app/schemas/` | Room şema dışa aktarımı (sürümleme) |

---

## 3. Teknoloji yığını

| Teknoloji | Kullanım |
|-----------|----------|
| Kotlin | Uygulama dili |
| Jetpack Compose | Arayüz |
| Material 3 | Tasarım sistemi; isteğe bağlı **Material You (dynamic color)** |
| Room | Yerel ilişkisel veri |
| DataStore (Preferences) | Uygulama tercihleri ve onboarding bayrağı |
| Hilt | Bağımlılık enjeksiyonu |
| Navigation Compose | Sekmeli ana akış |
| Coroutines / Flow | Asenkron veri ve UI durumu |
| KSP | Room ve Hilt için kod üretimi |

**Derleme:** `android-app` içinde `.\gradlew.bat assembleDebug`

---

## 4. Veri katmanı

### 4.1 Room

- Günlük formlar, su kayıtları vb. entity’ler DAO üzerinden okunur/yazılır.  
- Şema dışa aktarımı `app/schemas/` altında tutulur (migration disiplinine zemin).

### 4.2 DataStore

- `AppPreferences` benzeri yapı: su hedefi, tema, takip edilen metrikler, Material You anahtarı vb.  
- Onboarding tamamlanma bayrağı da burada izlenir (navigasyon kabuğu ile ilişkili).

---

## 5. Sunum katmanı (UI)

### 5.1 Compose ekranları

Örnek özellik paketleri:

- `feature/today` — günlük giriş ve metrik ızgarası  
- `feature/history` — geçmiş liste ve grafikler  
- `feature/report` — haftalık özet (skeleton + pull-to-refresh)  
- `feature/settings` — tercihler, dışa aktarma, hakkında  
- `feature/onboarding` — ilk kurulum

### 5.2 Ortak UI

`core/ui/components` altında kartlar, düğmeler, grafik bileşenleri vb. tekrar kullanılabilir parçalar.

### 5.3 Tema ve renk

- `MaterialTheme.colorScheme` — Material 3 yüzeyleri.  
- Ek `AppColorScheme` (CompositionLocal) — metrik bazlı **sabit** renkler; Material You açıkken bile metrik paleti tutarlı kalacak şekilde tasarlanmıştır.

### 5.4 Uyarlanabilirlik ve hareket

- Geniş ekranda (`screenWidthDp >= 600`) **NavigationRail**, dar ekranda **NavigationBar**.  
- Üst başlık için ortak **TopAppBar**; ekran içi tekrarlayan büyük başlıklar kaldırılarak çift başlık önlenmiştir.  
- Sistem animasyonları kapalıysa sekme geçişleri **anında** (reduced motion).

---

## 6. Paket ve kimlik

- Uygulama kimliği: `com.krayirhan.benimformum` (`applicationId` / `namespace` ile uyumlu).  
- Paket değişikliği yapıldıysa: cihazda **yeni uygulama** olarak görünür; eski yüklemeden otomatik taşınmaz.

---

## 7. Test ve kalite

- Birim testler: `./gradlew.bat testDebugUnitTest`  
- UI / instrumentation genişletme alanı: henüz minimal; CI’da smoke hedefi için genişletilebilir.

---

## 8. Güvenlik ve sürümleme notları

- Release imzalama ve mağaza süreçleri repo dışında tutulmalıdır (`*.jks`, `*.keystore` git’e girmez — `.gitignore` ile uyumlu).  
- Dışa aktarma **tamamen yerel**; ağ çağrısı yoktur (ürün vaadi ile uyumlu).

---

## 9. Bilinçli MVP sınırları (kod kapsamıyla uyumlu)

Aşağıdakiler **bilinçli olarak** MVP dışı bırakılmıştır: AI koç, bulut senkron, Health Connect, giyilebilir zorunluluk, SQLCipher, biyometrik kilit vb. Bu sınırlar teknik borcu kontrol altında tutar.

---

## 10. Özet

Benim Formum kod tarafında **katmanlı, test edilebilir ve Android ekosistemine yakın** modern bir yapıdadır. UI tarafında Compose + M3 ile tutarlı bir tema sistemi; veri tarafında Room + DataStore ile **yerel öncelikli** mimari net biçimde ayrılmıştır.

---

*Pazarlama ve ürün anlatımı için `PAZARLAMA-DEGERLENDIRME.md` dosyasına bakın.*
