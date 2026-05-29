package com.group02.mobile.data.repository

import com.group02.mobile.data.model.alphabet.KanaCharacter
import com.group02.mobile.data.model.alphabet.KanaRow
import com.group02.mobile.data.model.alphabet.KanaType

object KanaRepository {
    private val basicRows = listOf(
        KanaRow("row_a", "Hàng A", listOf(
            KanaCharacter("あ", "ア", "a", "あめ", "ame", "mưa", "アイス", "aisu", "kem"),
            KanaCharacter("い", "イ", "i", "いぬ", "inu", "con chó", "インク", "inku", "mực"),
            KanaCharacter("う", "ウ", "u", "うみ", "umi", "biển", "ウイルス", "uirusu", "vi-rút"),
            KanaCharacter("え", "エ", "e", "えき", "eki", "nhà ga", "エレベーター", "erebeetaa", "thang máy"),
            KanaCharacter("お", "オ", "o", "おにぎり", "onigiri", "cơm nắm", "オレンジ", "orenji", "quả cam")
        )),
        KanaRow("row_k", "Hàng K (Ka)", listOf(
            KanaCharacter("か", "カ", "ka", "かさ", "kasa", "cái ô", "カメラ", "kamera", "máy ảnh"),
            KanaCharacter("き", "キ", "ki", "きのこ", "kinoko", "nấm", "キウイ", "kiui", "quả kiwi"),
            KanaCharacter("く", "ク", "ku", "くるま", "kuruma", "ô tô", "クラス", "kurasu", "lớp học"),
            KanaCharacter("け", "ケ", "ke", "けいさつ", "keisatsu", "cảnh sát", "ケーキ", "keeki", "bánh ngọt"),
            KanaCharacter("こ", "コ", "ko", "こども", "kodomo", "trẻ em", "コーヒー", "koohii", "cà phê")
        )),
        KanaRow("row_s", "Hàng S (Sa)", listOf(
            KanaCharacter("さ", "サ", "sa", "さくら", "sakura", "hoa anh đào", "サラダ", "sarada", "món salad"),
            KanaCharacter("し", "シ", "shi", "しろ", "shiro", "màu trắng", "シャツ", "shatsu", "áo sơ mi"),
            KanaCharacter("す", "ス", "su", "すいか", "suika", "dưa hấu", "スープ", "suupu", "món súp"),
            KanaCharacter("せ", "セ", "se", "せんせい", "sensei", "giáo viên", "セーター", "seetaa", "áo len"),
            KanaCharacter("そ", "ソ", "so", "そら", "sora", "bầu trời", "ソース", "soosu", "nước xốt")
        )),
        KanaRow("row_t", "Hàng T (Ta)", listOf(
            KanaCharacter("た", "タ", "ta", "たべもの", "tabemono", "thức ăn", "タオル", "taoru", "khăn tắm"),
            KanaCharacter("ち", "チ", "chi", "ちかてつ", "chikatetsu", "tàu điện ngầm", "チーズ", "chiizu", "phô mai"),
            KanaCharacter("つ", "ツ", "tsu", "つくえ", "tsukue", "bàn", "ツアー", "tsuaa", "chuyến du lịch"),
            KanaCharacter("て", "テ", "te", "てがみ", "tegami", "bức thư", "テント", "tento", "cái lều"),
            KanaCharacter("と", "ト", "to", "とけい", "tokei", "đồng hồ", "トマト", "tomato", "cà chua")
        )),
        KanaRow("row_n", "Hàng N (Na)", listOf(
            KanaCharacter("な", "ナ", "na", "なつ", "natsu", "mùa hè", "ナイフ", "naifu", "con dao"),
            KanaCharacter("に", "ニ", "ni", "にく", "niku", "thịt", "ニュース", "nyuusu", "tin tức"),
            KanaCharacter("ぬ", "ヌ", "nu", "ぬいぐるみ", "nuigurumi", "thú nhồi bông", "ヌードル", "nuudoru", "mì"),
            KanaCharacter("ね", "ネ", "ne", "ねこ", "neko", "con mèo", "ネクタイ", "nekutai", "cà vạt"),
            KanaCharacter("の", "ノ", "no", "のみもの", "nomimono", "đồ uống", "ノート", "nooto", "vở")
        )),
        KanaRow("row_h", "Hàng H (Ha)", listOf(
            KanaCharacter("は", "ハ", "ha", "はな", "hana", "hoa", "ハンバーガー", "hanbaagaa", "bơ-gơ"),
            KanaCharacter("ひ", "ヒ", "hi", "ひこうき", "hikouki", "máy bay", "ヒーター", "hiitaa", "máy sưởi"),
            KanaCharacter("ふ", "フ", "fu", "ふね", "fune", "thuyền", "フライパン", "furaipan", "cái chảo"),
            KanaCharacter("へ", "ヘ", "he", "へや", "heya", "căn phòng", "ヘリコプター", "herikoputaa", "trực thăng"),
            KanaCharacter("ほ", "ホ", "ho", "ほん", "hon", "sách", "ホテル", "hoteru", "khách sạn")
        )),
        KanaRow("row_m", "Hàng M (Ma)", listOf(
            KanaCharacter("ま", "マ", "ma", "まど", "mado", "cửa sổ", "マスク", "masuku", "khẩu trang"),
            KanaCharacter("み", "ミ", "mi", "みず", "mizu", "nước", "ミルク", "miruku", "sữa"),
            KanaCharacter("む", "ム", "mu", "むし", "mushi", "côn trùng", "ムービー", "muubii", "phim ảnh"),
            KanaCharacter("め", "メ", "me", "めがね", "megane", "kính mắt", "メロン", "meron", "dưa lưới"),
            KanaCharacter("も", "モ", "mo", "もも", "momo", "quả đào", "モニター", "monitaa", "màn hình")
        )),
        KanaRow("row_y", "Hàng Y (Ya)", listOf(
            KanaCharacter("や", "ヤ", "ya", "やま", "yama", "núi", "タイヤ", "taiya", "lốp xe"),
            KanaCharacter("ゆ", "ユ", "yu", "ゆき", "yuki", "tuyết", "ユニフォーム", "yunifoomu", "đồng phục"),
            KanaCharacter("よ", "ヨ", "yo", "よる", "yoru", "buổi tối", "ヨーグルト", "yooguruto", "sữa chua")
        )),
        KanaRow("row_r", "Hàng R (Ra)", listOf(
            KanaCharacter("ら", "ラ", "ra", "らいおん", "raion", "sư tử", "ラジオ", "rajio", "đài radio"),
            KanaCharacter("り", "リ", "ri", "りんご", "ringo", "quả táo", "リボン", "ribon", "ruy băng"),
            KanaCharacter("る", "ル", "ru", "るす", "rusu", "vắng nhà", "ルビー", "rubii", "hồng ngọc"),
            KanaCharacter("れ", "レ", "re", "れいぞうこ", "reizouko", "tủ lạnh", "レモン", "remon", "quả chanh"),
            KanaCharacter("ろ", "ロ", "ro", "ろうそく", "rousoku", "cây nến", "ロボット", "robotto", "rô-bốt")
        )),
        KanaRow("row_w", "Hàng W (Wa)", listOf(
            KanaCharacter("わ", "ワ", "wa", "わたし", "watashi", "tôi", "ワイン", "wain", "rượu vang"),
            KanaCharacter("を", "ヲ", "wo", "", "", "", "ヲタク", "wotaku", "otaku (người hâm mộ)")
        )),
        KanaRow("row_nn", "Âm N đặc biệt", listOf(
            KanaCharacter("ん", "ン", "n", "", "", "", "ペン", "pen", "cái bút")
        ))
    )

    private val dakutenRows = listOf(
        KanaRow("row_g", "Hàng G (Ga) ゛", listOf(
            KanaCharacter("が", "ガ", "ga"),
            KanaCharacter("ぎ", "ギ", "gi"),
            KanaCharacter("ぐ", "グ", "gu"),
            KanaCharacter("げ", "ゲ", "ge"),
            KanaCharacter("ご", "ゴ", "go")
        )),
        KanaRow("row_z", "Hàng Z (Za) ゛", listOf(
            KanaCharacter("ざ", "ザ", "za"),
            KanaCharacter("じ", "ジ", "ji"),
            KanaCharacter("ず", "ズ", "zu"),
            KanaCharacter("ぜ", "ゼ", "ze"),
            KanaCharacter("ぞ", "ゾ", "zo")
        )),
        KanaRow("row_d", "Hàng D (Da) ゛", listOf(
            KanaCharacter("だ", "ダ", "da"),
            KanaCharacter("ぢ", "ヂ", "ji"),
            KanaCharacter("づ", "ヅ", "zu"),
            KanaCharacter("で", "デ", "de"),
            KanaCharacter("ど", "ド", "do")
        )),
        KanaRow("row_b", "Hàng B (Ba) ゛", listOf(
            KanaCharacter("ば", "バ", "ba"),
            KanaCharacter("び", "ビ", "bi"),
            KanaCharacter("ぶ", "ブ", "bu"),
            KanaCharacter("べ", "ベ", "be"),
            KanaCharacter("ぼ", "ボ", "bo")
        )),
        KanaRow("row_p", "Hàng P (Pa) ゜", listOf(
            KanaCharacter("ぱ", "パ", "pa"),
            KanaCharacter("ぴ", "ピ", "pi"),
            KanaCharacter("ぷ", "プ", "pu"),
            KanaCharacter("ぺ", "ペ", "pe"),
            KanaCharacter("ぽ", "ポ", "po")
        ))
    )

    private val youonRows = listOf(
        KanaRow("row_ky", "Hàng KY", listOf(
            KanaCharacter("きゃ", "キャ", "kya"), KanaCharacter("きゅ", "キュ", "kyu"), KanaCharacter("きょ", "キョ", "kyo")
        )),
        KanaRow("row_sy", "Hàng SY (Sha)", listOf(
            KanaCharacter("しゃ", "シャ", "sha"), KanaCharacter("しゅ", "シュ", "shu"), KanaCharacter("しょ", "ショ", "sho")
        )),
        KanaRow("row_ty", "Hàng TY (Cha)", listOf(
            KanaCharacter("ちゃ", "チャ", "cha"), KanaCharacter("ちゅ", "チュ", "chu"), KanaCharacter("ちょ", "チョ", "cho")
        )),
        KanaRow("row_ny", "Hàng NY", listOf(
            KanaCharacter("にゃ", "ニャ", "nya"), KanaCharacter("にゅ", "ニュ", "nyu"), KanaCharacter("にょ", "ニョ", "nyo")
        )),
        KanaRow("row_hy", "Hàng HY", listOf(
            KanaCharacter("ひゃ", "ヒャ", "hya"), KanaCharacter("ひゅ", "ヒュ", "hyu"), KanaCharacter("ひょ", "ヒョ", "hyo")
        )),
        KanaRow("row_my", "Hàng MY", listOf(
            KanaCharacter("みゃ", "ミャ", "mya"), KanaCharacter("みゅ", "ミュ", "myu"), KanaCharacter("みょ", "ミョ", "myo")
        )),
        KanaRow("row_ry", "Hàng RY", listOf(
            KanaCharacter("りゃ", "リャ", "rya"), KanaCharacter("りゅ", "リュ", "ryu"), KanaCharacter("りょ", "リョ", "ryo")
        )),
        KanaRow("row_gy", "Hàng GY", listOf(
            KanaCharacter("ぎゃ", "ギャ", "gya"), KanaCharacter("ぎゅ", "ギュ", "gyu"), KanaCharacter("ぎょ", "ギョ", "gyo")
        )),
        KanaRow("row_jy", "Hàng JY", listOf(
            KanaCharacter("じゃ", "ジャ", "ja"), KanaCharacter("じゅ", "ジュ", "ju"), KanaCharacter("じょ", "ジョ", "jo")
        )),
        KanaRow("row_by", "Hàng BY", listOf(
            KanaCharacter("びゃ", "ビャ", "bya"), KanaCharacter("びゅ", "ビュ", "byu"), KanaCharacter("びょ", "ビョ", "byo")
        )),
        KanaRow("row_py", "Hàng PY", listOf(
            KanaCharacter("ぴゃ", "ピャ", "pya"), KanaCharacter("ぴゅ", "ピュ", "pyu"), KanaCharacter("ぴょ", "ピョ", "pyo")
        ))
    )

    fun getAllRows(): List<KanaRow> = basicRows + dakutenRows + youonRows
    fun getBasicRows(): List<KanaRow> = basicRows
    fun getDakutenRows(): List<KanaRow> = dakutenRows
    fun getYouonRows(): List<KanaRow> = youonRows

    fun getRowById(rowId: String): KanaRow? {
        return getAllRows().find { it.rowId == rowId }
    }

    fun getCharacterDisplay(char: KanaCharacter, type: KanaType): String {
        return if (type == KanaType.HIRAGANA) char.hiragana else char.katakana
    }
}
