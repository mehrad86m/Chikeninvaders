# Chicken Invaders - پروژه نهایی درس AP

## نام دانشجو
(نام و نام‌خانوادگی خود را اینجا وارد کنید)

## پیش‌نیازها
- JDK 17 یا بالاتر (پروژه با OpenJDK 21 تست شده)
- هیچ کتابخانه‌ی خارجی لازم نیست (فقط Swing و javax.sound که جزو JDK هستند)

## ساختار پروژه
```
ChickenInvaders/
├── src/chickeninvaders/
│   ├── Main.java                  نقطه ورود برنامه
│   ├── GameMain.java              JFrame اصلی و CardLayout بین صفحات
│   ├── model/                     User, GameRecord
│   ├── db/DatabaseManager.java    پایگاه داده فایل‌متنی (database/users.txt, database/games.txt)
│   ├── sound/SoundManager.java    پخش و کنترل صداها
│   ├── ui/                        MainMenu, LoginPanel, RegisterPanel,
│   │                              HighScorePanel, SettingsPanel, HowToPlayPanel, StorePanel
│   ├── game/                      GamePanel (حلقه اصلی بازی), Plane, Bullet, Egg,
│   │                              Explosion, PowerUp(+Type), Cell, LevelConfig, PlaneType
│   └── enemy/                     Enemy(abstract) + NormalEnemy/FastEnemy/ZigzagEnemy/ShooterEnemy
│                                  Boss(abstract) + BossLevel4/BossLevel8
├── resources/
│   ├── images/                    (اختیاری - تصاویر خودتان را اینجا اضافه کنید)
│   └── sounds/                    background.wav, shoot.wav, crash.wav, gameover.wav, win.wav
└── database/                      به صورت خودکار هنگام اجرا ساخته می‌شود
```

## نحوه کامپایل و اجرا

### ویندوز (cmd)
```cmd
cd ChickenInvaders
mkdir bin
dir /s /b src\*.java > sources.txt
javac -d bin -encoding UTF-8 @sources.txt
java -cp bin chickeninvaders.Main
```

### لینوکس / مک (bash)
```bash
cd ChickenInvaders
mkdir -p bin
javac -d bin -encoding UTF-8 $(find src -name "*.java")
java -cp bin chickeninvaders.Main
```

### با فایل jar آماده
```bash
java -jar ChickenInvaders.jar
```
(دقت کنید پوشه‌های `resources/sounds` و `database` باید کنار jar در همان مسیر اجرا وجود داشته باشند تا صدا و ذخیره‌سازی کار کنند. اگر فایل‌های صوتی موجود نباشند، بازی بدون خطا و بدون صدا اجرا می‌شود.)

## کنترل‌های بازی
| کلید | عملکرد |
|---|---|
| → / D | حرکت به راست |
| ← / A | حرکت به چپ |
| ↑ / W | حرکت به بالا |
| ↓ / S | حرکت به پایین |
| Space | شلیک گلوله |
| P | توقف / ادامه بازی |
| Esc | بازگشت به منوی اصلی (پایان بازی جاری) |

## پایگاه داده
به‌جای SQLite (طبق اجازه صراحی صورت‌مسئله)، یک پایگاه داده‌ی فایل‌متنی پیشرفته با فرمت pipe-delimited پیاده‌سازی شده که در پوشه‌ی `database/` کنار برنامه ساخته می‌شود:

- **`database/users.txt`**: هر خط یک کاربر — `username|password|highScore|lastLevel|music|shot|crash|end|ownedPlane`
- **`database/games.txt`**: هر خط یک رکورد بازی انجام‌شده — `username|score|levelReached|timestamp|soundSettingsSummary`

جدول High Scores از روی `games.txt` و با نگه‌داشتن فقط بهترین امتیاز هر کاربر ساخته می‌شود. تمام عملیات خواندن/نوشتن `synchronized` هستند تا سازگاری داده حفظ شود.

## ویژگی‌های پیاده‌سازی‌شده
- ۸ مرحله کامل (۱-۳ و ۵-۷ معمولی با شبکه ۵×۸، مرحله ۴ غول میانی با ۵۰ ضربه و حمله ۴ جهته، مرحله ۸ غول نهایی با ۱۰۰ ضربه و حمله ۸ جهته)
- ۴ نوع دشمن (Normal, Fast, Zigzag, Shooter) با رفتار و امتیاز متفاوت
- مکانیزم جایگزینی مرغ‌ها بر اساس شمارنده‌ی هر خانه از شبکه
- ۵ نوع پاورآپ (Rapid Fire, Freeze Bomb, Extra Life, Shield, Add Fire دائمی و تجمعی) با احتمال ۲۰٪ ظهور
- افکت انفجار محو شونده برای نابودی دشمن/غول و برخورد به هواپیما
- سیستم صدا با ۴ کلید روشن/خاموش مجزا در Settings، ذخیره‌شده به ازای هر کاربر
- ثبت‌نام/ورود کاربر و جدول High Scores
- ذخیره‌ی هر بازی (امتیاز، آخرین سطح، تاریخ، تنظیمات صدا) در پایگاه داده
- جان‌ها بین مراحل ریست نمی‌شوند
- بخش اختیاری Store برای خرید هواپیماهای مختلف با امتیاز

## لینک ریپازیتوری گیت‌هاب
(لینک ریپازیتوری خود را اینجا اضافه کنید)
