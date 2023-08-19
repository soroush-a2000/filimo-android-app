package com.movieboxtv.app.config;

public class Config {

    // آدرس سایت شما
    public static final String API_URL = "";

    //کلید برنامه که باید در سمت سرور و اپ یکی باشد
    public static final String SECURE_KEY = "";

    // انتخاب نوع پرداخت
    public static final int PAYMENT_MOD = 3;  // ZarinPal = 3  Bazzar = 1

    // مرچند کد درگاه پرداخت
    public static String MERCHANT_CODE = "";
    public static String PAYMENT_DESCRIPTION = "خرید اشتراک";

    // کلید RSK بازار یا مایکت
    public static final String RSK_KEY = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwCjjO+9/3epzyYPPk5q4XDgZ+cZSYEQkuWG+p3CHkHkq+s4iMnO9nNbGXkomRZeQOHzBXclirhQazxbv6ZYVrsfSfNhxIE7IGAfHGhdB6m7yVdD9uI0aCT27F9M/OoaioKwuZDQHfCozh1TJv+xDMSSTmylV1MZDJ25bt8q2DRs1pAR7mQ7UUZECqIxLJokeJq8RuhWeCfMoqBETfWyaGKF7Vmi91JRtfFr5ZzKz7kCAwEAAQ==";

    // استایل صفحه اسپلش اسکرین
    public static int SPLASH_SCREEN = 2; // 1 = استایل قدیمی و 2 = استایل جدید

    // سوال امنیتی
    public static Boolean SECURITY_QUESTION = true; // true = فعال , false = غیر فعال

    // نویگیشن بار پایین برنامه
    public static int NAVIGATION_BAR = 2; // 1 = استایل قدیمی و 2 = استایل جدید

    // نشست های فعال : با فعال کردن این قابلیت هر حساب کاربری فقط میتواند با یک دستگاه لاگین کند.
    public static final Boolean ACTIVE_MEETINGS = false; // true = فعال , false = غیر فعال

    // برسی vpn : اگر میخواهید کاربرانی که از vpn استفاده میکنند اجازه ورود به اپلیکیشن را نداشته باشند این گزینه را فعال کنید
    public static final Boolean CHECK_VPN = true; // true = فعال , false = غیر فعال

    // گزینه های موضوع پشتیبانی (به ترتیب از چپ به راست)
    public static final String[] SUPPORT = { "موضوع درخواست","عدم شارژ شدن حساب کاربری","اشکالات برنامه","پیشنهاد فیلم و سریال","ارتباط با مدیریت","سایر موارد"};

    // قابلیت دانلود با ADM
    public static final int ADM_SUPPORT = 1;  //0 = غیر فعال  و  1 = فعال

    // قابلیت پخش با دیگر پخش کننده ها
    public static final int PLAYERS_SUPPORT = 1;  //0 = غیر فعال  و  1 = فعال

    // فعال یا غیر فعال کردن صفحه اینترو
    public static final Boolean ENABLE_INTRO = true;

    // اجبار به لاگین برای ورود به برنامه
    public static final Boolean COMPULSORY_LOGIN = false;

    //  فعال یا غیر فعال کردن ویرایش عکس پروفایل توسط کاربر
    public static final Boolean IMAGE_PROFILE_EDIT = true;

    // فعال یا غیر فعال کردن ویرایش اسم توسط کاربر
    public static final Boolean NAME_PROFILE_EDIT = true;

    // فعال یا غیر فعال کردن جعبه اعلان ها
    public static final Boolean BOX_NOTIFICAIONS_SETTING = false;

    // تنظیمات اسلایدر
    public static String AUTO_CHANGED_SLIDER = "TRUE";
    public static String SLIDER_CHANGE_TIME = "4000";

    // از اینجا دریافت کنید : https://console.developers.google.com/apis/credentials
    public static final String Youtube_Key = "AIzaSyAephi0fVTEBXgphX7Z_WVSW8iPusDibtg";

}