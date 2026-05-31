# Task.md – Kế hoạch Phát triển Tính năng Mới

## Tổng quan
Tài liệu này mô tả chi tiết hai tính năng mới cần triển khai trong ứng dụng Android học tiếng Nhật (Kotlin + Jetpack Compose, kiến trúc MVVM):

1. **Từ của tôi** – Kho từ vựng cá nhân của người dùng.
2. **Luyện tập Từ vựng Cá nhân** – Áp dụng các chế độ học vào bộ từ vựng tự chọn.

---

## Cấu trúc CSV mẫu (file `Tu_Vung_Tieng_Nhat.xlsx`)

| Kanji / Katakana | Hiragana | Romaji | Ý nghĩa (Tiếng Việt) 
| 家族 | かぞく | kazoku | Gia đình 

> File CSV import phải tuân theo đúng thứ tự cột này.

---

## TÍNH NĂNG 1 – Từ của tôi (My Vocabulary)

### Mục tiêu
Cho phép người dùng xây dựng và quản lý kho từ vựng tiếng Nhật cá nhân: thêm thủ công từng từ hoặc import hàng loạt từ file CSV, chỉnh sửa và xóa từ.

---

### 1.1. Data Layer

#### Data Class
**File:** `data/model/vocabulary/UserVocabulary.kt`

```kotlin
data class UserVocabulary(
    val id: String,                    // UUID tự sinh
    val kanji: String,                 // Kanji / Katakana (có thể rỗng)
    val hiragana: String,              // Hiragana
    val romaji: String,                // Romaji
    val meaning: String,               // Ý nghĩa tiếng Việt
    val studyCount: Int = 0,           // Số lần đã học
    val createdAt: Long,               // Timestamp tạo (ms)
    val updatedAt: Long                // Timestamp cập nhật (ms)
)
```

#### Repository
**File:** `data/repository/UserVocabularyRepository.kt`

Chức năng cần implement:
- `getAllVocabularies(): Flow<List<UserVocabulary>>` – Lấy toàn bộ từ vựng (realtime).
- `addVocabulary(item: UserVocabulary): Result<Unit>` – Thêm một từ.
- `updateVocabulary(item: UserVocabulary): Result<Unit>` – Cập nhật từ theo id.
- `deleteVocabulary(id: String): Result<Unit>` – Xóa từ theo id.
- `importFromCsv(csvContent: String): Result<Int>` – Parse CSV và thêm hàng loạt; trả về số từ đã import thành công.

> **Lưu ý lưu trữ:**  Tạo `UserVocabularyDao` và `AppDatabase` nếu chưa có. dùng Firebase Firesbase thì đồng bộ thêm lên Firesbase theo userId.
rule Firebase
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    match /users/{userId} {
      allow read, write: if request.auth != null
                         && request.auth.uid == userId;
    }

    match /profiles/{userId} {
      allow read, write: if request.auth != null
                         && request.auth.uid == userId;
    }
    match /UserVocabularies/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    match /MessagingNotification/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    // Từ vựng — ai cũng đọc được, chỉ admin mới ghi
    match /TuVung_Beginer/{document=**} {
      allow read: if true;
      allow write: if true; // Tắt sau khi import xong
    }
    match /TuVung_N5/{document=**} {
      allow read: if true;
      allow write: if true;
    }
    match /TuVung_N4/{document=**} {
      allow read: if true;
      allow write: if true;
    }
    match /TuVung_N3/{document=**} {
      allow read: if true;
      allow write: if true;
    }
    match /TuVung_N2/{document=**} {
      allow read: if true;
      allow write: if true;
    }
    match /TuVung_N1/{document=**} {
      allow read: if true;
      allow write: if true;
    }
    match /TuVung_Other/{document=**} {
      allow read: if true;
      allow write: if true;
    }
  }
}

#### CSV Parser (Utility)
**File:** `utils/CsvParser.kt`

- Đọc chuỗi CSV (UTF-8).
- Bỏ qua dòng header.
- Map theo thứ tự cột: `Kanji/Katakana`, `Hiragana`, `Romaji`, `Ý nghĩa`, (3 cột học bỏ qua).
- Validate: `hiragana` và `meaning` không được rỗng; bỏ qua dòng lỗi, ghi log.
- Trả về `List<UserVocabulary>`.

---

### 1.2. ViewModel
**File:** `viewmodel/UserVocabularyViewModel.kt`

```
State:
- vocabularyList: StateFlow<List<UserVocabulary>>
- uiState: StateFlow<UiState>   // Loading | Success | Error(msg)
- searchQuery: StateFlow<String>
- filteredList: StateFlow<List<UserVocabulary>>  // derived từ list + query

Events / Functions:
- onSearchQueryChange(query: String)
- addVocabulary(kanji, hiragana, romaji, meaning)
- updateVocabulary(item: UserVocabulary)
- deleteVocabulary(id: String)
- importFromCsv(uri: Uri, context: Context)
  → đọc file từ Uri, gọi CsvParser, gọi repository
- confirmDelete(id: String)   // hiện dialog xác nhận
```

---

### 1.3. UI – Màn hình danh sách (MyVocabularyScreen)
**File:** `ui/screens/vocabulary/MyVocabularyScreen.kt`

**Layout:**
```
TopAppBar: "Từ của tôi"
SearchBar: TextField tìm kiếm theo từ hoặc nghĩa
LazyColumn:
  → Mỗi item: hiển thị [Kanji] / [Hiragana] – [Nghĩa]
  → Swipe-to-delete hoặc nút icon Edit / Delete ở cuối item
FAB (FloatingActionButton): nút "+" → điều hướng đến AddVocabularyScreen
```

**Trạng thái hiển thị:**
- Danh sách rỗng → hiển thị Empty State ("Chưa có từ vựng nào. Hãy thêm từ đầu tiên!").
- Loading → CircularProgressIndicator.
- Error → Snackbar với thông báo lỗi.

---

### 1.4. UI – Màn hình Thêm/Chỉnh sửa từ (AddEditVocabularyScreen)
**File:** `ui/screens/vocabulary/AddEditVocabularyScreen.kt`

**Tham số route:** `?vocabularyId={id}` (null = thêm mới, có giá trị = chỉnh sửa)

**Layout:**
```
TopAppBar: "Thêm từ mới" / "Chỉnh sửa từ"
TextField: Kanji / Katakana (không bắt buộc)
TextField: Hiragana* (bắt buộc)
TextField: Romaji
TextField: Ý nghĩa (Tiếng Việt)* (bắt buộc)
Divider + Text: "Hoặc import từ file CSV"
Button: "Chọn file CSV" → mở file picker (ActivityResultContracts.GetContent, type "text/*")
  → Preview: hiển thị số dòng hợp lệ tìm được trong file
Button: "Import [X] từ" → gọi importFromCsv
Button (primary): "Lưu" (disabled nếu field bắt buộc rỗng)
```

**Validation:**
- Hiragana và Ý nghĩa không được để trống → hiển thị error text dưới TextField.
- Hiragana chỉ chứa ký tự kana (cảnh báo nhẹ, không chặn).

---

### 1.5. Navigation
**File:** `navigation/AuthNavGraph.kt` (bổ sung routes)

```kotlin
// Thêm các route sau:
const val MY_VOCABULARY_ROUTE = "my_vocabulary"
const val ADD_EDIT_VOCABULARY_ROUTE = "add_edit_vocabulary?vocabularyId={vocabularyId}"
```

**Tích hợp vào DictionaryScreen:**
- Thêm tab hoặc nút "Từ của tôi" trên `DictionaryScreen.kt`.
- Nhấn → navigate đến `MyVocabularyScreen`.

---

## TÍNH NĂNG 2 – Luyện tập Từ vựng Cá nhân (Custom Practice)

### Mục tiêu
Cho phép người dùng chọn nguồn từ vựng (từ "Từ của tôi" hoặc nhập tạm thời) rồi luyện tập qua 4 chế độ đã có: Học từ, Flashcard, Trắc nghiệm, Thử thách.

---

### 2.1. Data Layer

#### Data Class – Session từ vựng
**File:** `data/model/vocabulary/PracticeSession.kt`

```kotlin
data class PracticeSession(
    val id: String,
    val name: String,                         // VD: "Luyện tập 25/05/2025"
    val vocabularyIds: List<String>,           // id từ UserVocabulary (nếu nguồn là "Từ của tôi")
    val temporaryVocabularies: List<UserVocabulary>, // Từ nhập tạm (không lưu vào kho)
    val mode: PracticeMode,
    val createdAt: Long
)

enum class PracticeMode {
    STUDY, FLASHCARD, QUIZ, CHALLENGE
}
```

---

### 2.2. ViewModel
**File:** `viewmodel/CustomPracticeViewModel.kt`

```
State:
- selectedVocabularies: StateFlow<List<UserVocabulary>>
- practiceMode: StateFlow<PracticeMode?>
- sessionReady: StateFlow<Boolean>
- importError: StateFlow<String?>

Functions:
- loadFromMyVocabulary(ids: List<String>)     // Load từ UserVocabularyRepository
- loadAllMyVocabularies()                      // Chọn toàn bộ kho
- importFromCsv(uri: Uri, context: Context)   // Import tạm, không lưu kho
- addManualVocabulary(item: UserVocabulary)   // Thêm từ tạm thời
- removeVocabulary(id: String)
- clearSelection()
- setPracticeMode(mode: PracticeMode)
- startSession(): PracticeSession             // Build session, navigate đến màn hình luyện tập
```

---

### 2.3. UI – Màn hình Thiết lập Luyện tập (CustomPracticeSetupScreen)
**File:** `ui/screens/vocabulary/CustomPracticeSetupScreen.kt`

**Layout:**
```
TopAppBar: "Luyện tập Từ vựng"

── PHẦN 1: Chọn nguồn từ vựng ──
Card "Từ của tôi":
  [ ] Dùng toàn bộ kho từ vựng (toggle)
  [ ] Chọn từng từ → mở BottomSheet chọn từ với checkbox

Card "Nhập thủ công":
  Button: "Thêm từ" → mở mini dialog nhập nhanh (Hiragana + Nghĩa)
  Button: "Import CSV" → file picker → preview số từ

── PHẦN 2: Bộ từ đã chọn ──
Text: "[X] từ đã chọn"
LazyRow: chip hiển thị từng từ (có nút x để bỏ)

── PHẦN 3: Chọn chế độ luyện tập ──
Row gồm 4 Card/Button:
  [📖 Học từ]  [🃏 Flashcard]  [📝 Trắc nghiệm]  [⚡ Thử thách]

Button (primary, disabled nếu chưa chọn từ và chế độ): "Bắt đầu"
```

---

### 2.4. Tích hợp vào các màn hình Luyện tập hiện có

Các màn hình hiện tại (`StudyScreen`, `FlashCardScreen`, `QuizScreen`, `ChallengeScreen`) cần hỗ trợ nhận danh sách từ vựng từ bên ngoài (qua ViewModel hoặc Navigation argument).

**Cách tiếp cận đề xuất:**

1. Tạo một `SharedPracticeViewModel` (hoặc sử dụng `SavedStateHandle`) để truyền `List<UserVocabulary>` vào các màn hình luyện tập.
2. Mỗi màn hình kiểm tra: nếu có `customVocabularyList` → dùng list đó; ngược lại dùng nguồn dữ liệu mặc định (Kana, Kanji từ `KanaRepository`).

**File cần sửa:**
- `StudyScreen.kt` – nhận `List<UserVocabulary>?` làm tham số tùy chọn.
- `FlashCardScreen.kt` – tương tự.
- `QuizScreen.kt` – sinh câu hỏi trắc nghiệm từ `List<UserVocabulary>`.
- `ChallengeScreen.kt` – tương tự.
- `KanaViewModel.kt` (hoặc tạo `SharedPracticeViewModel.kt`) – giữ danh sách từ của session hiện tại.

---

### 2.5. Navigation
**File:** `navigation/AuthNavGraph.kt` (bổ sung)

```kotlin
const val CUSTOM_PRACTICE_SETUP_ROUTE = "custom_practice_setup"
```

**Entry point:** Thêm nút/card "Luyện tập Tùy chỉnh" trên `HomeScreen.kt` hoặc `AlphabetHomeScreen.kt`.

---

## Thứ tự triển khai đề xuất

| Bước | Công việc | File liên quan |
|---|---|---|
| 1 | Tạo Room Entity + Dao + Database cho `UserVocabulary` | `data/local/` |
| 2 | Implement `CsvParser.kt` | `utils/CsvParser.kt` |
| 3 | Implement `UserVocabularyRepository.kt` | `data/repository/` |
| 4 | Implement `UserVocabularyViewModel.kt` | `viewmodel/` |
| 5 | Xây dựng `MyVocabularyScreen.kt` | `ui/screens/vocabulary/` |
| 6 | Xây dựng `AddEditVocabularyScreen.kt` | `ui/screens/vocabulary/` |
| 7 | Tích hợp tab/nút vào `DictionaryScreen.kt` | `ui/screens/alphabet/` |
| 8 | Cập nhật Navigation routes | `navigation/AuthNavGraph.kt` |
| 9 | Tạo `CustomPracticeViewModel.kt` | `viewmodel/` |
| 10 | Xây dựng `CustomPracticeSetupScreen.kt` | `ui/screens/vocabulary/` |
| 11 | Sửa các màn hình luyện tập nhận custom vocabulary | `ui/screens/alphabet/` |
| 12 | Test end-to-end toàn bộ flow | – |

---

## Lưu ý kỹ thuật chung

- **File picker CSV:** Dùng `ActivityResultContracts.GetContent("text/*")` hoặc `"*/*"` rồi validate extension phía app.
- **Encoding:** File CSV phải là UTF-8 (ghi rõ trong UI hướng dẫn người dùng).
- **Xử lý lỗi import:** Hiển thị dialog kết quả sau khi import ("Đã thêm X từ, bỏ qua Y dòng lỗi").
- **Không lưu từ tạm:** Từ nhập vào phần luyện tập tạm thời (Tính năng 2) **không** tự động lưu vào kho "Từ của tôi"; hiển thị tùy chọn "Lưu vào kho?" sau khi kết thúc session.
- **TtsManager:** Tái sử dụng `TtsManager.kt` đã có để phát âm trong `StudyScreen` và `FlashCardScreen` khi dùng custom vocabulary.
