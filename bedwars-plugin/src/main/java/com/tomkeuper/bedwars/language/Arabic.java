package com.tomkeuper.bedwars.language;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.Messages;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Arabic extends Language {
    public Arabic() {
        super(BedWars.plugin, "ar");
        YamlConfiguration yml = getYml();
        yml.options().copyDefaults(true);
        yml.options().header("this language are translated by: abbflaabb");
        yml.addDefault(Messages.PREFIX, "");
        yml.addDefault("name", "Arabic");
        yml.addDefault(Messages.COMMAND_MAIN, Arrays.asList(
                "",
                "&2▪ &7/" + BedWars.mainCmd + " الاحصائيات",
                "&2▪ &7/" + BedWars.mainCmd + " انضم &o<الساحة/المجموعة>",
                "&2▪ &7/" + BedWars.mainCmd + " غادر",
                "&2▪ &7/" + BedWars.mainCmd + " اللغة",
                "&2▪ &7/" + BedWars.mainCmd + " القائمة",
                "&2▪ &7/" + BedWars.mainCmd + " ابدأ &3(فيفي)"
        ));

        yml.addDefault(Messages.COMMAND_LANG_LIST_HEADER, "%bw_lang_prefix% &2اللغات المتاحة:");
        yml.addDefault(Messages.COMMAND_LANG_LIST_FORMAT, "&a▪  &7%bw_lang_iso% - &f%bw_name%");
        yml.addDefault(Messages.COMMAND_LANG_USAGE, "%bw_lang_prefix%&7الاستخدام: /lang &f&o<رمز اللغة>");
        yml.addDefault(Messages.COMMAND_LANG_SELECTED_NOT_EXIST, "%bw_lang_prefix%&cهذه اللغة غير موجودة!");
        yml.addDefault(Messages.COMMAND_LANG_SELECTED_SUCCESSFULLY, "%bw_lang_prefix%&aتم تغيير اللغة بنجاح!");
        yml.addDefault(Messages.COMMAND_LANG_USAGE_DENIED, "%bw_lang_prefix%&cلا يمكنك تغيير اللغة أثناء اللعب.");

        yml.addDefault(Messages.COMMAND_JOIN_USAGE, "&a▪ &7الاستخدام: /" + BedWars.mainCmd + " انضم &o<الساحة/المجموعة>");
        yml.addDefault(Messages.COMMAND_JOIN_GROUP_OR_ARENA_NOT_FOUND, "%bw_lang_prefix%&cلا توجد ساحة أو مجموعة باسم: %bw_name%");
        yml.addDefault(Messages.COMMAND_JOIN_DENIED_IS_FULL, "%bw_lang_prefix%&cهذه الساحة ممتلئة!\n&aيرجى التفكير بالتبرع لمزيد من الميزات. &7&o(اضغط)");
        yml.addDefault(Messages.COMMAND_JOIN_NO_EMPTY_FOUND, "%bw_lang_prefix%&cلا توجد ساحات متاحة الآن ;(");
        yml.addDefault(Messages.COMMAND_JOIN_DENIED_IS_FULL_OF_VIPS, "%bw_lang_prefix%&cنعتذر لكن هذه الساحة ممتلئة.\n&cنعلم أنك متبرع لكن هذه الساحة مليئة بالموظفين و/أو المتبرعين.");
        yml.addDefault(Messages.COMMAND_JOIN_DENIED_PARTY_TOO_BIG, "%bw_lang_prefix%&cحزمتك كبيرة جدًا للانضمام إلى هذه الساحة كفريق :(");
        yml.addDefault(Messages.COMMAND_JOIN_DENIED_NOT_PARTY_LEADER, "%bw_lang_prefix%&cفقط قائد الحزمة يمكنه اختيار الساحة.");
        yml.addDefault(Messages.COMMAND_JOIN_PLAYER_JOIN_MSG, "%bw_lang_prefix%&7%bw_player% &eانضم (&b%bw_on%&e/&b%bw_max%&e)!");
        yml.addDefault(Messages.COMMAND_JOIN_SPECTATOR_MSG, "%bw_lang_prefix%&6أنت الآن متفرج على &9%bw_arena%&6.\n%bw_lang_prefix%&eيمكنك مغادرة الساحة في أي وقت باستخدام &c/غادر&e.");
        yml.addDefault(Messages.COMMAND_JOIN_SPECTATOR_DENIED_MSG, "&cالمتفرجون غير مسموح لهم في هذه الساحة!");

        yml.addDefault(Messages.COMMAND_TP_PLAYER_NOT_FOUND, "%bw_lang_prefix%&cلم يتم العثور على اللاعب!");
        yml.addDefault(Messages.COMMAND_TP_NOT_IN_ARENA, "%bw_lang_prefix%&cهذا اللاعب ليس في ساحة BedWars!");
        yml.addDefault(Messages.COMMAND_TP_NOT_STARTED, "%bw_lang_prefix%&cالساحة التي فيها اللاعب لم تبدأ بعد!");
        yml.addDefault(Messages.COMMAND_TP_USAGE, "%bw_lang_prefix%&cالاستخدام: /bw tp <اسم اللاعب>");

        yml.addDefault(Messages.REJOIN_NO_ARENA, "%bw_lang_prefix%&cلا توجد ساحة لإعادة الانضمام!");
        yml.addDefault(Messages.REJOIN_DENIED, "%bw_lang_prefix%&cلا يمكنك إعادة الانضمام للساحة بعد الآن. انتهت اللعبة أو تم تدمير السرير.");
        yml.addDefault(Messages.REJOIN_ALLOWED, "%bw_lang_prefix%&eجارٍ الانضمام إلى الساحة &a%bw_arena%&e!");
        yml.addDefault(Messages.COMMAND_REJOIN_PLAYER_RECONNECTED, "%bw_lang_prefix%&7%bw_player% &eأعاد الاتصال!");

        yml.addDefault(Messages.COMMAND_LEAVE_DENIED_NOT_IN_ARENA, "%bw_lang_prefix%&cأنت لست في أي ساحة!");
        yml.addDefault(Messages.COMMAND_LEAVE_MSG, "%bw_lang_prefix%&7%bw_player% &eغادر!");
        yml.addDefault(Messages.COMMAND_LEAVE_HAS_PARTY_POPUP_TITLE, "هل أنت متأكد؟");
        yml.addDefault(Messages.COMMAND_LEAVE_HAS_PARTY_POPUP_STAY, "&cلا");
        yml.addDefault(Messages.COMMAND_LEAVE_HAS_PARTY_POPUP_STAY_LORE, List.of("&fالبقاء في الساحة"));
        yml.addDefault(Messages.COMMAND_LEAVE_HAS_PARTY_POPUP_BRING_PARTY, "&aنعم");
        yml.addDefault(Messages.COMMAND_LEAVE_HAS_PARTY_POPUP_BRING_PARTY_LORE, List.of("&fاستدعاء الحزمة معك"));

        yml.addDefault(Messages.COMMAND_NOT_ALLOWED_IN_GAME, "%bw_lang_prefix%&cلا يمكنك فعل ذلك أثناء اللعبة.");
        yml.addDefault(Messages.COMMAND_NOT_FOUND_OR_INSUFF_PERMS, "%bw_lang_prefix%&cالأمر غير موجود أو ليس لديك الصلاحيات!");

        yml.addDefault(Messages.COMMAND_PARTY_HELP, Arrays.asList(
                "&6▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                "&aأوامر الحزمة:",
                "&e/party help &7- &bعرض رسالة المساعدة هذه",
                "&e/party invite <player> &7- &bدعوة اللاعب للانضمام لحزمتك",
                "&e/party leave &7- &bمغادرة الحزمة الحالية",
                "&e/party remove <player> &7- &bإزالة اللاعب من الحزمة",
                "&e/party info &7- &bعرض أعضاء الحزمة وصاحبها",
                "&e/party promote <player> &7- &bنقل ملكية الحزمة",
                "&e/party accept <player> &7- &bقبول دعوة الحزمة",
                "&e/party disband &7- &bحل الحزمة"
        ));
        yml.addDefault(Messages.RESOURCE_CHEST_BLOCKED_ITEM, "&cلا يمكنك وضع &f{item} &cفي صندوق الموارد!");
        yml.addDefault(Messages.RESOURCE_CHEST_FULL, "&cصندوق الموارد ممتلئ!");
        yml.addDefault(Messages.RESOURCE_CHEST_DEPOSITED, "&aلقد وضعت &f{amount} {item} &aفي {chest}!");

        yml.addDefault(Messages.COMMAND_PARTY_INVITE_USAGE, "%bw_lang_prefix%&eالاستخدام: &7/party invite <player>");
        yml.addDefault(Messages.COMMAND_PARTY_INVITE_DENIED_PLAYER_OFFLINE, "%bw_lang_prefix%&7%bw_player% &eغير متصل!");
        yml.addDefault(Messages.COMMAND_PARTY_INVITE_SENT, "%bw_lang_prefix%&eتم إرسال الدعوة إلى &7%bw_player%&6.");
        yml.addDefault(Messages.COMMAND_PARTY_INVITE_SENT_TARGET_RECEIVE_MSG, "%bw_lang_prefix%&b%bw_player% &eدعاك للانضمام إلى الحزمة! &o&7(اضغط للقبول)");
        yml.addDefault(Messages.COMMAND_PARTY_INVITE_DENIED_CANNOT_INVITE_YOURSELF, "%bw_lang_prefix%&cلا يمكنك دعوة نفسك!");
        yml.addDefault(Messages.COMMAND_PARTY_ACCEPT_DENIED_NO_INVITE, "%bw_lang_prefix%&cلا توجد دعوات للانضمام!");
        yml.addDefault(Messages.COMMAND_PARTY_ACCEPT_DENIED_ALREADY_IN_PARTY, "%bw_lang_prefix%&eأنت بالفعل في حزمة!");
        yml.addDefault(Messages.COMMAND_PARTY_INSUFFICIENT_PERMISSIONS, "%bw_lang_prefix%&cفقط صاحب الحزمة يمكنه فعل ذلك!");
        yml.addDefault(Messages.COMMAND_PARTY_ACCEPT_USAGE, "%bw_lang_prefix%&eالاستخدام: &7/party accept <player>");
        yml.addDefault(Messages.COMMAND_PARTY_ACCEPT_SUCCESS, "%bw_lang_prefix%&7%bw_player% &eانضم إلى الحزمة!");
        yml.addDefault(Messages.COMMAND_PARTY_GENERAL_DENIED_NOT_IN_PARTY, "%bw_lang_prefix%&cأنت لست في أي حزمة!");
        yml.addDefault(Messages.COMMAND_PARTY_LEAVE_DENIED_IS_OWNER_NEEDS_DISBAND, "%bw_lang_prefix%&cلا يمكنك مغادرة حزمتك!\n&eحاول استخدام: &b/party disband");
        yml.addDefault(Messages.COMMAND_PARTY_LEAVE_SUCCESS, "%bw_lang_prefix%&7%bw_player% &eغادر الحزمة!");
        yml.addDefault(Messages.COMMAND_PARTY_DISBAND_SUCCESS, "%bw_lang_prefix%&eتم حل الحزمة!");
        yml.addDefault(Messages.COMMAND_PARTY_REMOVE_USAGE, "%bw_lang_prefix%&7الاستخدام: &e/party remove <player>");
        yml.addDefault(Messages.COMMAND_PARTY_REMOVE_SUCCESS, "%bw_lang_prefix%&7%bw_player% &eتمت إزالته من الحزمة");
        yml.addDefault(Messages.COMMAND_PARTY_REMOVE_DENIED_TARGET_NOT_PARTY_MEMBER, "%bw_lang_prefix%&7%bw_player% &eليس في حزمتك!");
        yml.addDefault(Messages.COMMAND_PARTY_PROMOTE_SUCCESS, "%bw_lang_prefix%&eتم ترقية %bw_player% ليصبح صاحب الحزمة");
        yml.addDefault(Messages.COMMAND_PARTY_PROMOTE_OWNER, "%bw_lang_prefix%&eتمت ترقيتك لتصبح صاحب الحزمة");
        yml.addDefault(Messages.COMMAND_PARTY_PROMOTE_NEW_OWNER, "%bw_lang_prefix%&7 &e%bw_player% أصبح الآن صاحب الحزمة");
        yml.addDefault(Messages.COMMAND_PARTY_INFO_OWNER, "\n%bw_lang_prefix%&eصاحب الحزمة: &7%bw_party_owner%");
        yml.addDefault(Messages.COMMAND_PARTY_INFO_PLAYERS, "%bw_lang_prefix%&eأعضاء الحزمة:");
        yml.addDefault(Messages.COMMAND_PARTY_INFO_PLAYER, "&7%bw_player%");
        yml.addDefault(Messages.COMMAND_PARTY_CHAT_ENABLED_DISABLED, "&e&lPARTY &8&l┃ &fدردشة الحزمة الآن %bw_party_chat_status% &f!");

        yml.addDefault(Messages.COMMAND_FORCESTART_NOT_IN_GAME, "&c▪ &7أنت لست في اللعبة!");
        yml.addDefault(Messages.COMMAND_FORCESTART_SUCCESS, "&c▪ &7تم تقصير العد التنازلي!");
        yml.addDefault(Messages.COMMAND_FORCESTART_NO_PERM, "%bw_lang_prefix%&7لا يمكنك بدء الساحة بالقوة.\n&7يرجى التفكير بالتبرع لمزايا VIP.");
        yml.addDefault(Messages.COMMAND_COOLDOWN, "&cلا يمكنك فعل ذلك الآن! انتظر %bw_seconds% ثانية أخرى!");
        yml.addDefault(Messages.COMMAND_SHOUT_DISABLE_SOLO, "&cالصراخ معطل في اللعب الفردي!");
        yml.addDefault(Messages.COMMAND_LEAVE_STARTED, "&a&lجارٍ نقلك إلى اللوبي خلال %bw_leave_delay% ثانية... اضغط بالزر الأيمن مرة أخرى لإلغاء النقل!");
        yml.addDefault(Messages.COMMAND_LEAVE_CANCELED, "&c&lتم إلغاء النقل!");
        yml.addDefault(Messages.ARENA_JOIN_VIP_KICK, "%bw_lang_prefix%&cتم طردك لأن متبرع انضم إلى الساحة.\n&aيرجى التفكير بالتبرع لمزيد من الميزات. &7&o(اضغط)");
        yml.addDefault(Messages.ARENA_START_COUNTDOWN_STOPPED_INSUFF_PLAYERS_CHAT, "%bw_lang_prefix%&cلا يوجد عدد كافٍ من اللاعبين! تم إيقاف العد التنازلي!");
        yml.addDefault(Messages.ARENA_RESTART_PLAYER_KICK, "%bw_lang_prefix%&eالساحة التي كنت فيها تعيد التشغيل.");

        yml.addDefault(Messages.ARENA_STATUS_PLAYING_NAME, "&cجاري اللعب");
        yml.addDefault(Messages.ARENA_STATUS_RESTARTING_NAME, "&4جارٍ إعادة التشغيل");
        yml.addDefault(Messages.ARENA_STATUS_WAITING_NAME, "&2انتظار &c%bw_full%");
        yml.addDefault(Messages.ARENA_STATUS_STARTING_NAME, "&6بدء &c%bw_full%");

        yml.addDefault(Messages.ARENA_GUI_INV_NAME, "&8اضغط للانضمام");
        yml.addDefault(Messages.ARENA_GUI_ARENA_CONTENT_NAME, "&a&l%bw_name%");
        yml.addDefault(Messages.ARENA_GUI_ARENA_CONTENT_LORE, Arrays.asList(
                "",
                "&7الحالة: %bw_arena_status%",
                "&7اللاعبون: &f%bw_on%&7/&f%bw_max%",
                "&7النوع: &a%bw_group%",
                "",
                "&aانقر بالزر الأيسر للانضمام.",
                "&eانقر بالزر الأيمن للمشاهدة."
        ));
        yml.addDefault(Messages.ARENA_GUI_SKIPPED_ITEM_NAME, "&r%bw_server_ip%");
        yml.addDefault(Messages.ARENA_GUI_SKIPPED_ITEM_LORE, Collections.emptyList());

        yml.addDefault(Messages.ARENA_STATUS_START_COUNTDOWN_CHAT, "%bw_lang_prefix%&eتبدأ اللعبة خلال &6%bw_time% &eثانية!");
        yml.addDefault(Messages.ARENA_STATUS_START_COUNTDOWN_TITLE, " ");
        yml.addDefault(Messages.ARENA_STATUS_START_COUNTDOWN_SUB_TITLE, "&a%bw_seconds%");
        yml.addDefault(Messages.ARENA_STATUS_START_COUNTDOWN_SUB_TITLE + "-5", "&e❺");
        yml.addDefault(Messages.ARENA_STATUS_START_COUNTDOWN_SUB_TITLE + "-4", "&e❹");
        yml.addDefault(Messages.ARENA_STATUS_START_COUNTDOWN_SUB_TITLE + "-3", "&c❸");
        yml.addDefault(Messages.ARENA_STATUS_START_COUNTDOWN_SUB_TITLE + "-2", "&c❷");
        yml.addDefault(Messages.ARENA_STATUS_START_COUNTDOWN_SUB_TITLE + "-1", "&c❶");
        yml.addDefault(Messages.ARENA_STATUS_START_COUNTDOWN_CANCELLED_TITLE, " ");
        yml.addDefault(Messages.ARENA_STATUS_START_COUNTDOWN_CANCELLED_SUB_TITLE, "&cانتظار المزيد من اللاعبين..");

        yml.addDefault(Messages.ARENA_STATUS_START_PLAYER_TITLE, "&aانطلق!");
        yml.addDefault(Messages.ARENA_STATUS_START_PLAYER_TUTORIAL, Arrays.asList(
                "&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                "&f                                   &lBedWars",
                "",
                "&e&l    احمِ سريرك ودمّر أسرّة الأعداء.",
                "&e&l      طور نفسك وفريقك بجمع",
                "&e&l   الحديد، الذهب، الزمرد، والألماس من المولدات",
                "&e&l             للوصول إلى ترقيات قوية.",
                "",
                "&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        ));
        yml.addDefault(Messages.ARENA_JOIN_DENIED_SELECTOR, "%bw_lang_prefix%&cعذرًا، لا يمكنك الانضمام لهذه الساحة الآن. استخدم النقر بالزر الأيمن للمشاهدة!");
        yml.addDefault(Messages.ARENA_SPECTATE_DENIED_SELECTOR, "%bw_lang_prefix%&cعذرًا، لا يمكنك مشاهدة هذه الساحة الآن. استخدم النقر بالزر الأيسر للانضمام!");
        yml.addDefault(Messages.ARENA_JOIN_DENIED_NO_PROXY, "&cعذرًا، يجب الانضمام إلى الساحة عبر BedWarsProxy.\n&eإذا أردت إعداد ساحة، تأكد من إعطاء نفسك صلاحية bw.setup لتتمكن من الانضمام مباشرةً!");
        yml.addDefault(Messages.ARENA_JOIN_DENIED_NO_TIME, "&cعذرًا، لقد انضممت بينما كانت اللعبة قد بدأت بالفعل.");

        yml.addDefault(Messages.ARENA_SPECTATOR_TELEPORTER_GUI_NAME, "&8جهاز النقل");
        yml.addDefault(Messages.ARENA_SPECTATOR_TELEPORTER_GUI_HEAD_NAME, "%bw_v_prefix%%bw_player%");
        yml.addDefault(Messages.ARENA_SPECTATOR_TELEPORTER_GUI_HEAD_LORE, Arrays.asList("&7الصحة: &f%bw_player_health%%", "&7الجوع: &f%bw_player_food%", "", "&7انقر بالزر الأيسر للمشاهدة"));

        yml.addDefault(Messages.ARENA_SPECTATOR_LEAVE_ITEM_NAME, "&c&lالعودة إلى اللوبي");
        yml.addDefault(Messages.ARENA_SPECTATOR_LEAVE_ITEM_LORE, Collections.singletonList("&7انقر بالزر الأيمن للعودة إلى اللوبي!"));

        yml.addDefault(Messages.ARENA_SPECTATOR_FIRST_PERSON_ENTER_TITLE, "&aتشاهد &7%bw_player%");
        yml.addDefault(Messages.ARENA_SPECTATOR_FIRST_PERSON_ENTER_SUBTITLE, "&cانحنِ للخروج");
        yml.addDefault(Messages.ARENA_SPECTATOR_FIRST_PERSON_LEAVE_TITLE, "&eالخروج من وضع المشاهدة");
        yml.addDefault(Messages.ARENA_SPECTATOR_FIRST_PERSON_LEAVE_SUBTITLE, "");

        yml.addDefault(Messages.ARENA_LEAVE_PARTY_DISBANDED, "%bw_lang_prefix%&cلقد غادر صاحب الحزمة وتم حل الحزمة!");

        yml.addDefault(Messages.GENERATOR_HOLOGRAM_TIER, "&eالمستوى &c%bw_tier%");
        yml.addDefault(Messages.GENERATOR_HOLOGRAM_TYPE_DIAMOND, "&b&lألماس");
        yml.addDefault(Messages.GENERATOR_HOLOGRAM_TYPE_EMERALD, "&a&lزمرد");
        yml.addDefault(Messages.GENERATOR_HOLOGRAM_TIMER, "&eيتولد خلال &c%bw_seconds% &eثوانٍ");
        yml.addDefault(Messages.GENERATOR_UPGRADE_CHAT_ANNOUNCEMENT, "%bw_lang_prefix%%bw_generator_type% مولدات &eتم ترقيتها إلى المستوى &c%bw_tier%");

        yml.addDefault(Messages.FORMATTING_CHAT_LOBBY, "%bw_level%%bw_v_prefix%&7%bw_player%%bw_v_suffix%: %bw_message%");
        yml.addDefault(Messages.FORMATTING_CHAT_WAITING, "%bw_level%%bw_v_prefix%&7%bw_player%%bw_v_suffix%: %bw_message%");
        yml.addDefault(Messages.FORMATTING_CHAT_SHOUT, "%bw_level%%bw_v_prefix%&6[صراخ] %bw_team_format% &7%bw_player%&f%bw_v_suffix%: %bw_message%");
        yml.addDefault(Messages.FORMATTING_CHAT_TEAM, "%bw_level%%bw_v_prefix%&f%bw_team_format%&7 %bw_player%%bw_v_suffix%: %bw_message%");
        yml.addDefault(Messages.FORMATTING_CHAT_SPECTATOR, "%bw_level%%bw_v_prefix%&7[مشاهد] %bw_player%%bw_v_suffix%: %bw_message%");

        yml.addDefault(Messages.FORMATTING_SCOREBOARD_TAB_PREFIX_SPECTATOR, List.of("&7"));
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_TAB_SUFFIX_SPECTATOR, new ArrayList<>());
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_TAB_PREFIX_RESTARTING, Arrays.asList("%bw_team_color%&l%bw_team_letter% &r%bw_team_color%", "%bw_team% ", "%bw_v_prefix% %bw_team_color%"));
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_TAB_SUFFIX_RESTARTING, new ArrayList<>());
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_TAB_PREFIX_PLAYING, Arrays.asList("%bw_team_color%&l%bw_team_letter% &r%bw_team_color%", "%bw_team% ", "%bw_v_prefix% %bw_team_color%&l%bw_team_letter% &r%bw_team_color%"));
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_TAB_SUFFIX_PLAYING, new ArrayList<>());
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_TAB_PREFIX_STARTING, List.of("%bw_v_prefix% "));
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_TAB_SUFFIX_STARTING, new ArrayList<>());
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_TAB_PREFIX_WAITING, List.of("%bw_v_prefix% "));
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_TAB_SUFFIX_WAITING, new ArrayList<>());
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_TAB_PREFIX_LOBBY, List.of("%bw_v_prefix% "));
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_TAB_SUFFIX_LOBBY, new ArrayList<>());

        yml.addDefault(Messages.FORMATTING_SCOREBOARD_HEAD_PREFIX_RESTARTING, List.of("%bw_team_color%&l%bw_team_letter% &r%bw_team_color%"));
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_HEAD_SUFFIX_RESTARTING, new ArrayList<>());
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_HEAD_PREFIX_PLAYING, List.of("%bw_team_color%&l%bw_team_letter% &r%bw_team_color%"));
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_HEAD_SUFFIX_PLAYING, new ArrayList<>());
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_HEAD_PREFIX_STARTING, List.of("%bw_v_prefix% "));
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_HEAD_SUFFIX_STARTING, new ArrayList<>());
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_HEAD_PREFIX_WAITING, List.of("%bw_v_prefix% "));
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_HEAD_SUFFIX_WAITING, new ArrayList<>());
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_HEAD_PREFIX_LOBBY, List.of("%bw_v_prefix% "));
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_HEAD_SUFFIX_LOBBY, new ArrayList<>());

        yml.addDefault(Messages.FORMATTING_SIDEBAR_TAB_HEADER_LOBBY, "&6%bw_server_ip%\n");
        yml.addDefault(Messages.FORMATTING_SIDEBAR_TAB_HEADER_WAITING, "&a%bw_server_ip%\n");
        yml.addDefault(Messages.FORMATTING_SIDEBAR_TAB_HEADER_STARTING, "&6%bw_server_ip%\n");
        yml.addDefault(Messages.FORMATTING_SIDEBAR_TAB_HEADER_PLAYING, "&d%bw_server_ip%\n");
        yml.addDefault(Messages.FORMATTING_SIDEBAR_TAB_HEADER_RESTARTING, "&c%bw_server_ip%\n");
        yml.addDefault(Messages.FORMATTING_SIDEBAR_TAB_HEADER_SPECTATOR, "&9%bw_server_ip%\n");

        yml.addDefault(Messages.FORMATTING_SIDEBAR_TAB_FOOTER_LOBBY, "\n&6%bw_server_ip%");
        yml.addDefault(Messages.FORMATTING_SIDEBAR_TAB_FOOTER_WAITING, "\n&a%bw_server_ip%");
        yml.addDefault(Messages.FORMATTING_SIDEBAR_TAB_FOOTER_STARTING, "\n&6%bw_server_ip%");
        yml.addDefault(Messages.FORMATTING_SIDEBAR_TAB_FOOTER_PLAYING, "\n&d%bw_server_ip%");
        yml.addDefault(Messages.FORMATTING_SIDEBAR_TAB_FOOTER_RESTARTING, "\n&c%bw_server_ip%");
        yml.addDefault(Messages.FORMATTING_SIDEBAR_TAB_FOOTER_SPECTATOR, "\n&9%bw_server_ip%");

        yml.addDefault(Messages.FORMATTING_SCOREBOARD_DATE, "dd/MM/yy");
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_TEAM_GENERIC, "%bw_team_color%%bw_team_letter%&f %bw_team_name%: %bw_team_status%");
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_TEAM_ELIMINATED, "&c&l✘");
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_BED_DESTROYED, "&a%bw_players_remaining%");
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_TEAM_ALIVE, "&a&l✓");
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_NEXEVENT_TIMER, "mm:ss");
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_YOUR_TEAM, "&7أنت");
        yml.addDefault(Messages.FORMATTING_ACTION_BAR_TRACKING, "&fتتبع: %bw_team% &f- المسافة: %bw_distance%m");
        yml.addDefault(Messages.FORMATTING_BOSSBAR_DRAGON, "%bw_team% &7التنين");
        yml.addDefault(Messages.FORMATTING_TEAM_WINNER_FORMAT, "      %bw_team_color%%bw_team_name% &7- %bw_winner_members%");
        yml.addDefault(Messages.FORMATTING_SOLO_WINNER_FORMAT, "                 %bw_team_color%%bw_team_name% &7- %bw_winner_members%");
        yml.addDefault(Messages.FORMATTING_GENERATOR_TIER1, "I");
        yml.addDefault(Messages.FORMATTING_GENERATOR_TIER2, "II");
        yml.addDefault(Messages.FORMATTING_GENERATOR_TIER3, "III");
        yml.addDefault(Messages.FORMATTING_DESPAWNABLE_UTILITY_NPC_HEALTH, "▮ ");
        yml.addDefault(Messages.FORMATTING_STATS_DATE_FORMAT, "yyyy/MM/dd HH:mm");
        yml.addDefault(Messages.FORMAT_PAPI_PLAYER_TEAM_TEAM, "%bw_team_color%[%bw_team_name%]");
        yml.addDefault(Messages.FORMAT_PAPI_PLAYER_TEAM_SHOUT, "&6[صراخ]");
        yml.addDefault(Messages.FORMAT_PAPI_PLAYER_TEAM_SPECTATOR, "&7[مشاهد]");

        yml.addDefault(Messages.MEANING_FULL, "ممتلئ");
        yml.addDefault(Messages.MEANING_SHOUT, "صراخ");
        yml.addDefault(Messages.MEANING_NOBODY, "لا أحد");
        yml.addDefault(Messages.MEANING_NEVER, "أبدًا");
        yml.addDefault(Messages.MEANING_IRON_SINGULAR, "حديد");
        yml.addDefault(Messages.MEANING_IRON_PLURAL, "حديد");
        yml.addDefault(Messages.MEANING_GOLD_SINGULAR, "ذهب");
        yml.addDefault(Messages.MEANING_GOLD_PLURAL, "ذهب");
        yml.addDefault(Messages.MEANING_EMERALD_SINGULAR, "زمرد");
        yml.addDefault(Messages.MEANING_EMERALD_PLURAL, "زمرد");
        yml.addDefault(Messages.MEANING_DIAMOND_SINGULAR, "ألماس");
        yml.addDefault(Messages.MEANING_DIAMOND_PLURAL, "ألماس");
        yml.addDefault(Messages.MEANING_VAULT_SINGULAR, "$");
        yml.addDefault(Messages.MEANING_VAULT_PLURAL, "$");
        yml.addDefault(Messages.MEANING_ENABLED, "&aمفعل");
        yml.addDefault(Messages.MEANING_DISABLED, "&cمعطل");

        yml.addDefault(Messages.INTERACT_CANNOT_PLACE_BLOCK, "%bw_lang_prefix%&cلا يمكنك وضع الكتل هنا!");
        yml.addDefault(Messages.INTERACT_CANNOT_BREAK_BLOCK, "%bw_lang_prefix%&cيمكنك فقط كسر الكتل التي وضعها لاعب!");
        yml.addDefault(Messages.INTERACT_FULL_CHEST, "%bw_lang_prefix%&cالصندوق ممتلئ!");
        yml.addDefault(Messages.INTERACT_CANNOT_BREAK_OWN_BED, "&cلا يمكنك تدمير سريرك!");
        yml.addDefault(Messages.INTERACT_BED_DESTROY_CHAT_ANNOUNCEMENT, "\n&f&lتدمير السرير > %bw_team_color%%bw_team_name% تم تدمير سريره بواسطة %bw_player_color%%bw_player%&7!\n");
        yml.addDefault(Messages.INTERACT_BED_DESTROY_TITLE_ANNOUNCEMENT, "&cتم تدمير السرير!");
        yml.addDefault(Messages.INTERACT_BED_DESTROY_SUBTITLE_ANNOUNCEMENT, "&fلن تستطيع العودة بعد الآن!");
        yml.addDefault(Messages.INTERACT_BED_DESTROY_CHAT_ANNOUNCEMENT_TO_VICTIM, "&f&lتدمير السرير > &7تم تدمير سريرك بواسطة %bw_player_color%%bw_player%&7!");

        yml.addDefault(Messages.INTERACT_CHEST_CANT_OPEN_TEAM_ELIMINATED, "&cلا يمكنك فتح هذا الصندوق لأن هذه الفريق لم يُقصى بعد!");
        yml.addDefault(Messages.INTERACT_INVISIBILITY_REMOVED_DAMGE_TAKEN, "&cلم تعد غير مرئي لأنك تلقيت ضررًا!");
        yml.addDefault(Messages.INTERACT_MAGIC_MILK_REMOVED, "&cانتهت مدة الحليب السحري!");
        yml.addDefault(Messages.EGGBRIDGE_BUILD_LIMIT_WARNING, "&cأنت قريب جدًا من حد البناء!");

        yml.addDefault(Messages.PLAYER_DIE_VOID_FALL_REGULAR_KILL, "%bw_player_color%%bw_player% &7سقط في الفراغ.");
        yml.addDefault(Messages.PLAYER_DIE_VOID_FALL_FINAL_KILL, "%bw_player_color%%bw_player% &7سقط في الفراغ. &b&lالقتل النهائي!");
        yml.addDefault(Messages.PLAYER_DIE_KNOCKED_IN_VOID_REGULAR_KILL, "%bw_player_color%%bw_player% &7تم دفعه إلى الفراغ بواسطة %bw_killer_color%%bw_killer_name%&7.");
        yml.addDefault(Messages.PLAYER_DIE_KNOCKED_IN_VOID_FINAL_KILL, "%bw_player_color%%bw_player% &7تم دفعه إلى الفراغ بواسطة %bw_killer_color%%bw_killer_name%&7. &b&lالقتل النهائي!");
        yml.addDefault(Messages.PLAYER_DIE_PVP_LOG_OUT_REGULAR, "%bw_player_color%%bw_player% &7انقطع الاتصال أثناء القتال مع %bw_killer_color%%bw_killer_name%&7.");
        yml.addDefault(Messages.PLAYER_DIE_PVP_LOG_OUT_FINAL, "%bw_player_color%%bw_player% &7انقطع الاتصال أثناء القتال مع %bw_killer_color%%bw_killer_name%&7. &b&lالقتل النهائي!");
        yml.addDefault(Messages.PLAYER_DIE_KNOCKED_BY_REGULAR_KILL, "%bw_player_color%%bw_player% &7تم دفعه بواسطة %bw_killer_color%%bw_killer_name%&7.");
        yml.addDefault(Messages.PLAYER_DIE_KNOCKED_BY_FINAL_KILL, "%bw_player_color%%bw_player% &7تم دفعه بواسطة %bw_killer_color%%bw_killer_name%&7. &b&lالقتل النهائي!");
        yml.addDefault(Messages.PLAYER_DIE_EXPLOSION_WITH_SOURCE_REGULAR_KILL, "%bw_player_color%%bw_player% &7تلقى ضربة من قنبلة حب بواسطة %bw_killer_color%%bw_killer_name%&7.");
        yml.addDefault(Messages.PLAYER_DIE_EXPLOSION_WITH_SOURCE_FINAL_KILL, "%bw_player_color%%bw_player% &7تلقى ضربة من قنبلة حب بواسطة %bw_killer_color%%bw_killer_name%&7. &b&lالقتل النهائي!");
        yml.addDefault(Messages.PLAYER_DIE_EXPLOSION_WITHOUT_SOURCE_REGULAR, "%bw_player_color%%bw_player% &7تلقى ضربة من قنبلة.");
        yml.addDefault(Messages.PLAYER_DIE_EXPLOSION_WITHOUT_SOURCE_FINAL_KILL, "%bw_player_color%%bw_player% &7تلقى ضربة من قنبلة. &b&lالقتل النهائي!");
        yml.addDefault(Messages.PLAYER_DIE_PVP_REGULAR_KILL, "%bw_player_color%%bw_player% &7قُتل بواسطة %bw_killer_color%%bw_killer_name%&7.");
        yml.addDefault(Messages.PLAYER_DIE_PVP_FINAL_KILL, "%bw_player_color%%bw_player% &7قُتل بواسطة %bw_killer_color%%bw_killer_name%&7. &b&lالقتل النهائي!");
        yml.addDefault(Messages.PLAYER_DIE_UNKNOWN_REASON_REGULAR, "%bw_player_color%%bw_player% &7توفي.");
        yml.addDefault(Messages.PLAYER_DIE_UNKNOWN_REASON_FINAL_KILL, "%bw_player_color%%bw_player% &7توفي. &b&lالقتل النهائي!");
        yml.addDefault(Messages.PLAYER_DIE_SHOOT_REGULAR, "%bw_player_color%%bw_player% &7تم إطلاق النار عليه بواسطة %bw_killer_color%%bw_killer_name%&7!");
        yml.addDefault(Messages.PLAYER_DIE_SHOOT_FINAL_KILL, "%bw_player_color%%bw_player% &7تم إطلاق النار عليه بواسطة %bw_killer_color%%bw_killer_name%&7! &b&lالقتل النهائي!");

        yml.addDefault(Messages.PLAYER_DIE_DEBUG_REGULAR, "%bw_player_color%%bw_player% &7تم قتله بواسطة BedBug لفريق %bw_killer_color%%bw_killer_team_name%!");
        yml.addDefault(Messages.PLAYER_DIE_DEBUG_FINAL_KILL, "%bw_player_color%%bw_player% &7تم قتله بواسطة BedBug لفريق %bw_killer_color%%bw_killer_team_name%! &b&lالقتل النهائي!");
        yml.addDefault(Messages.PLAYER_DIE_IRON_GOLEM_REGULAR, "%bw_player_color%%bw_player% &7تم قتله بواسطة Iron Golem لفريق %bw_killer_color%%bw_killer_team_name%!");
        yml.addDefault(Messages.PLAYER_DIE_IRON_GOLEM_FINAL_KILL, "%bw_player_color%%bw_player% &7تم قتله بواسطة Iron Golem لفريق %bw_killer_color%%bw_killer_team_name%! &b&lالقتل النهائي!");

        yml.addDefault(Messages.PLAYER_DIE_REWARD_DIAMOND, "%bw_lang_prefix%&b+%bw_amount% %bw_meaning%");
        yml.addDefault(Messages.PLAYER_DIE_REWARD_EMERALD, "%bw_lang_prefix%&a+%bw_amount% %bw_meaning%");
        yml.addDefault(Messages.PLAYER_DIE_REWARD_IRON, "%bw_lang_prefix%&f+%bw_amount% %bw_meaning%");
        yml.addDefault(Messages.PLAYER_DIE_REWARD_GOLD, "%bw_lang_prefix%&6+%bw_amount% %bw_meaning%");

        yml.addDefault(Messages.ARENA_MAX_BUILD_LIMIT_REACHED, "&cتم الوصول إلى الحد الأعلى للبناء!");
        yml.addDefault(Messages.ARENA_MIN_BUILD_LIMIT_REACHED, "&cتم الوصول إلى الحد الأدنى للبناء!");
        yml.addDefault(Messages.ARENA_FIREBALL_COOLDOWN, "&cانتظر %bw_cooldown%s لاستخدام ذلك مرة أخرى!");

        yml.addDefault(Messages.ARENA_IN_GAME_ANNOUNCEMENT, Arrays.asList(
                "&c&lإذا انقطع الاتصال استخدم /rejoin للعودة إلى اللعبة.",
                "&c&lالتحالف بين الفرق غير مسموح! أبلغ عن المخالفين باستخدام /report."
        ));

        yml.addDefault(Messages.PLAYER_DIE_RESPAWN_TITLE, "&cلقد مت!");
        yml.addDefault(Messages.PLAYER_DIE_RESPAWN_SUBTITLE, "&eستعود للحياة خلال &c%bw_time% &eثوانٍ!");
        yml.addDefault(Messages.PLAYER_DIE_RESPAWN_CHAT, "%bw_lang_prefix%&eستعود للحياة خلال &c%bw_time% &eثوانٍ!");
        yml.addDefault(Messages.PLAYER_DIE_RESPAWNED_TITLE, "&aلقد عدت للحياة!");
        yml.addDefault(Messages.PLAYER_DIE_ELIMINATED_CHAT, "%bw_lang_prefix%&cتم إقصاؤك!");
        yml.addDefault(Messages.PLAYER_DIE_RESPAWNED_TEXT, "%bw_lang_prefix%&eلقد عدت للحياة!");

        yml.addDefault(Messages.PLAYER_HIT_BOW, "%bw_lang_prefix%%bw_player% &7على &c%bw_damage_amount% &7HP!");

        yml.addDefault(Messages.GAME_END_GAME_OVER_PLAYER_TITLE, "&c&lانتهت اللعبة!");
        yml.addDefault(Messages.GAME_END_VICTORY_PLAYER_TITLE, "&6&lالنصر!");
        yml.addDefault(Messages.GAME_END_TEAM_WON_CHAT, "%bw_lang_prefix%%bw_team_color%%bw_team_name% &aفازوا باللعبة!");
        yml.addDefault(Messages.GAME_END_NO_WINNERS, "&cلا فائزين هذه المرة!");
        yml.addDefault(Messages.FORMATTING_EACH_WINNER, "%bw_player%");
        yml.addDefault(Messages.GAME_END_FIRST_KILLER, "%bw_player%");
        yml.addDefault(Messages.GAME_END_SECOND_KILLER, "%bw_player%");
        yml.addDefault(Messages.GAME_END_THIRD_KILLER, "%bw_player%");
        yml.addDefault(Messages.GAME_END_TOP_PLAYER_CHAT, Arrays.asList(
                "&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                "&f                                   &lBedWars", "", "%bw_winner_format%", "", "",
                "&e                          &lالقاتل الأول &7- %bw_first_format% - %bw_first_kills%",
                "&6                          &lالقاتل الثاني &7- %bw_second_format% - %bw_second_kills%",
                "&c                          &lالقاتل الثالث &7- %bw_third_format% - %bw_third_kills%", "",
                "&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        ));
        yml.addDefault(Messages.BED_HOLOGRAM_DEFEND, "&c&lاحم سريرك!");
        yml.addDefault(Messages.BED_HOLOGRAM_DESTROYED, "&c&lتم تدمير سريرك!");
        yml.addDefault(Messages.TEAM_ELIMINATED_CHAT, "\n&f&lالفريق المُقصى > %bw_team_color%%bw_team_name% &cتم إقصاؤه!\n");

        yml.addDefault(Messages.NEXT_EVENT_BEDS_DESTROY, "&fتم تدمير الأسرّة");
        yml.addDefault(Messages.NEXT_EVENT_DIAMOND_UPGRADE_II, "&fألماس II");
        yml.addDefault(Messages.NEXT_EVENT_DIAMOND_UPGRADE_III, "&fألماس III");
        yml.addDefault(Messages.NEXT_EVENT_DRAGON_SPAWN, "&fالموت المفاجئ");
        yml.addDefault(Messages.NEXT_EVENT_EMERALD_UPGRADE_II, "&fزمرد II");
        yml.addDefault(Messages.NEXT_EVENT_EMERALD_UPGRADE_III, "&fزمرد III");
        yml.addDefault(Messages.NEXT_EVENT_GAME_END, "&4نهاية اللعبة");

        yml.addDefault(Messages.NEXT_EVENT_TITLE_ANNOUNCE_BEDS_DESTROYED, "&cتم تدمير الأسرّة!");
        yml.addDefault(Messages.NEXT_EVENT_SUBTITLE_ANNOUNCE_BEDS_DESTROYED, "&fتم تدمير جميع الأسرّة!");
        yml.addDefault(Messages.NEXT_EVENT_CHAT_ANNOUNCE_BEDS_DESTROYED, "&c&lتم تدمير جميع الأسرّة!");

        yml.addDefault(Messages.NEXT_EVENT_TITLE_ANNOUNCE_SUDDEN_DEATH, "&cالموت المفاجئ");
        yml.addDefault(Messages.NEXT_EVENT_SUBTITLE_ANNOUNCE_SUDDEN_DEATH, "");
        yml.addDefault(Messages.NEXT_EVENT_CHAT_ANNOUNCE_SUDDEN_DEATH, "&cالموت المفاجئ: &6&b%bw_dragons_amount% %bw_team_color%%bw_team_name% التنين!");

        yml.addDefault(Messages.XP_REWARD_PER_MINUTE, "%bw_lang_prefix%&6+%bw_xp% خبرة BedWars (مدة اللعب).");
        yml.addDefault(Messages.XP_REWARD_WIN, "%bw_lang_prefix%&6+%bw_xp% خبرة BedWars (الفوز باللعبة).");
        yml.addDefault(Messages.XP_REWARD_PER_TEAMMATE, "%bw_lang_prefix%&6+%bw_xp% خبرة BedWars (دعم الفريق).");
        yml.addDefault(Messages.XP_REWARD_BED_DESTROY, "%bw_lang_prefix%&6+%bw_xp% خبرة BedWars (تدمير سرير).");
        yml.addDefault(Messages.XP_REWARD_REGULAR_KILL, "%bw_lang_prefix%&6+%bw_xp% خبرة BedWars (قتل عادي).");

        yml.addDefault(Messages.XP_REWARD_FINAL_KILL, "%bw_lang_prefix%&6+%bw_xp% خبرة BedWars (القتل النهائي).");
        //removed HALLOWEEN in Arabic because it is not celebrated in Arabic countries
      //  yml.addDefault(Messages.XP_REWARD_HALLOWEEN, "%bw_lang_prefix%&6+5 خبرة BedWars (هالوين).");

        yml.addDefault(Messages.PLAYER_LEVEL_UP, Collections.singletonList("&aتهانينا! لقد وصلت إلى المستوى %bw_level%."));

        yml.addDefault(Messages.MONEY_REWARD_PER_MINUTE, "%bw_lang_prefix%&6+%bw_money% عملة (مدة اللعب).");
        yml.addDefault(Messages.MONEY_REWARD_WIN, "%bw_lang_prefix%&6+%bw_money% عملة (الفوز باللعبة).");
        yml.addDefault(Messages.MONEY_REWARD_PER_TEAMMATE, "%bw_lang_prefix%&6+%bw_money% عملة (دعم الفريق).");
        yml.addDefault(Messages.MONEY_REWARD_BED_DESTROYED, "%bw_lang_prefix%&6+%bw_money% عملة (تدمير سرير).");
        yml.addDefault(Messages.MONEY_REWARD_FINAL_KILL, "%bw_lang_prefix%&6+%bw_money% عملة (القتل النهائي).");
        yml.addDefault(Messages.MONEY_REWARD_REGULAR_KILL, "%bw_lang_prefix%&6+%bw_money% عملة (قتل عادي).");

        //removed HALLOWEEN in Arabic because it is not celebrated in Arabic countries
//        yml.addDefault(Messages.HALLOWEEN_ITEM_NAME, "&6&lعيد هالوين سعيد");

        /* Lobby Command Items */
        yml.addDefault(Messages.GENERAL_CONFIGURATION_LOBBY_ITEMS_NAME.replace("%path%", "stats"), "&eالإحصائيات");
        yml.addDefault(Messages.GENERAL_CONFIGURATION_LOBBY_ITEMS_LORE.replace("%path%", "stats"), Collections.singletonList("&fانقر بزر الفأرة الأيمن لرؤية إحصائياتك!"));
        yml.addDefault(Messages.GENERAL_CONFIGURATION_LOBBY_ITEMS_NAME.replace("%path%", "arena-selector"), "&eاختيار الساحة");
        yml.addDefault(Messages.GENERAL_CONFIGURATION_LOBBY_ITEMS_LORE.replace("%path%", "arena-selector"), Collections.singletonList("&fانقر بزر الفأرة الأيمن لاختيار الساحة!"));
        yml.addDefault(Messages.GENERAL_CONFIGURATION_LOBBY_ITEMS_NAME.replace("%path%", "leave"), "&eالعودة إلى الهب");
        yml.addDefault(Messages.GENERAL_CONFIGURATION_LOBBY_ITEMS_LORE.replace("%path%", "leave"), Collections.singletonList("&fانقر بزر الفأرة الأيمن لمغادرة BedWars!"));
        /* Pre Game Command Items */
        yml.addDefault(Messages.GENERAL_CONFIGURATION_WAITING_ITEMS_NAME.replace("%path%", "stats"), "&eالإحصائيات");
        yml.addDefault(Messages.GENERAL_CONFIGURATION_WAITING_ITEMS_LORE.replace("%path%", "stats"), Collections.singletonList("&fانقر بزر الفأرة الأيمن لرؤية إحصائياتك!"));
        yml.addDefault(Messages.GENERAL_CONFIGURATION_WAITING_ITEMS_NAME.replace("%path%", "leave"), "&eالعودة إلى اللوبي");
        yml.addDefault(Messages.GENERAL_CONFIGURATION_WAITING_ITEMS_LORE.replace("%path%", "leave"), Collections.singletonList("&fانقر بزر الفأرة الأيمن لمغادرة الساحة!"));
        /* Spectator Command Items */
        yml.addDefault(Messages.GENERAL_CONFIGURATION_SPECTATOR_ITEMS_NAME.replace("%path%", "teleporter"), "&eالمنقل");
        yml.addDefault(Messages.GENERAL_CONFIGURATION_SPECTATOR_ITEMS_NAME.replace("%path%", "leave"), "&eالعودة إلى اللوبي");
        yml.addDefault(Messages.GENERAL_CONFIGURATION_SPECTATOR_ITEMS_LORE.replace("%path%", "leave"), Collections.singletonList("&fانقر بزر الفأرة الأيمن لمغادرة الساحة!"));
// GUI إحصائيات اللاعب
        yml.addDefault(Messages.PLAYER_STATS_GUI_INV_NAME, "&8إحصائيات %bw_player%");
        addDefaultStatsMsg(yml, "wins", "&6عدد الفوز", "&f%bw_wins%");
        addDefaultStatsMsg(yml, "losses", "&6عدد الخسائر", "&f%bw_losses%");
        addDefaultStatsMsg(yml, "kills", "&6عدد القتلات", "&f%bw_kills%");
        addDefaultStatsMsg(yml, "deaths", "&6عدد الوفيات", "&f%bw_deaths%");
        addDefaultStatsMsg(yml, "final-kills", "&6القتل النهائي", "&f%bw_final_kills%");
        addDefaultStatsMsg(yml, "final-deaths", "&6الوفاة النهائية", "&f%bw_final_deaths%");
        addDefaultStatsMsg(yml, "beds-destroyed", "&6الأسرّة المدمرة", "&f%bw_beds%");
        addDefaultStatsMsg(yml, "first-play", "&6أول مرة لعب", "&f%bw_play_first%");
        addDefaultStatsMsg(yml, "last-play", "&6آخر مرة لعب", "&f%bw_play_last%");
        addDefaultStatsMsg(yml, "games-played", "&6عدد الألعاب", "&f%bw_games_played%");

        yml.addDefault(Messages.SCOREBOARD_DEFAULT_WAITING, Arrays.asList(
                "&f&lBED WARS",
                "&7%bw_date% &8%bw_server_id%",
                "",
                "&fالخريطة: &a%bw_map%",
                "",
                "&fعدد اللاعبين: &a%bw_on%/%bw_max%",
                "",
                "&fفي الانتظار...",
                "",
                "&fالوضع: &a%bw_group%",
                "&fالإصدار: &7%bw_version%",
                "",
                "&e%bw_server_ip%"
        ));

        yml.addDefault(Messages.SCOREBOARD_DEFAULT_STARTING, Arrays.asList(
                "&f&lBED WARS",
                "&7%bw_date% &8%bw_server_id%",
                "",
                "&fالخريطة: &a%bw_map%",
                "",
                "&fعدد اللاعبين: &a%bw_on%/%bw_max%",
                "",
                "&fستبدأ اللعبة خلال &a%bw_time%s",
                "",
                "&fالوضع: &a%bw_group%",
                "&fالإصدار: &7%bw_version%",
                "",
                "&e%bw_server_ip%"
        ));

        yml.addDefault(Messages.SCOREBOARD_DEFAULT_PLAYING, Arrays.asList(
                "&e&lBED WARS",
                "&7%bw_date%",
                "",
                "&f%bw_next_event% خلال &a%bw_time%",
                "",
                "%bw_team_1%", "%bw_team_2%", "%bw_team_3%", "%bw_team_4%", "%bw_team_5%", "%bw_team_6%", "%bw_team_7%", "%bw_team_8%",
                "",
                "&e%bw_server_ip%"
        ));

        yml.addDefault("scoreboard.Doubles.playing", Arrays.asList(
                "&e&lBED WARS", "&7%bw_date%", "", "&f%bw_next_event% خلال &a%bw_time%", "",
                "%bw_team_1%", "%bw_team_2%", "%bw_team_3%", "%bw_team_4%", "%bw_team_5%", "%bw_team_6%", "%bw_team_7%", "%bw_team_8%", "",
                "&e%bw_server_ip%"
        ));

        yml.addDefault("scoreboard.3v3v3v3.playing", Arrays.asList(
                "&e&lBED WARS", "&7%bw_date%", "", "&f%bw_next_event% خلال &a%bw_time%", "",
                "%bw_team_1%", "%bw_team_2%", "%bw_team_3%", "%bw_team_4%", "",
                "&fالقتلات: &a%bw_kills%", "&fالقتل النهائي: &a%bw_final_kills%", "&fالأسرّة المدمرة: &a%bw_beds%", "",
                "&e%bw_server_ip%"
        ));

        yml.addDefault("scoreboard.4v4v4v4.playing", Arrays.asList(
                "&e&lBED WARS", "&7%bw_date%", "", "&f%bw_next_event% خلال &a%bw_time%", "",
                "%bw_team_1%", "%bw_team_2%", "%bw_team_3%", "%bw_team_4%", "",
                "&fالقتلات: &a%bw_kills%", "&fالقتل النهائي: &a%bw_final_kills%", "&fالأسرّة المدمرة: &a%bw_beds%", "",
                "&e%bw_server_ip%"
        ));
        yml.addDefault(Messages.SCOREBOARD_LOBBY, Arrays.asList(
                "&6&lBedWars",
                "&fمستواك: %bw_level%",
                "",
                "&fالتقدم: &a%bw_current_xp%&7/&b%bw_required_xp%",
                "%bw_progress%",
                "",
                "&7%player%",
                "",
                "&fالعملات: &a%bw_money%",
                "",
                "&fإجمالي الفوز: &a%bw_wins%",
                "&fإجمالي القتلات: &a%bw_kills%",
                "",
                "&e%bw_server_ip%"
        ));

        yml.addDefault(Messages.SHOP_INDEX_NAME, "&8شراء سريع");
        yml.addDefault(Messages.SHOP_QUICK_ADD_NAME, "&8يتم الإضافة للشراء السريع...");
        yml.addDefault(Messages.SHOP_INSUFFICIENT_MONEY, "%bw_lang_prefix%&cليس لديك ما يكفي من %bw_currency%! تحتاج %bw_amount% أكثر!");
        yml.addDefault(Messages.SHOP_NEW_PURCHASE, "%bw_lang_prefix%&aلقد اشتريت &6%bw_item%");
        yml.addDefault(Messages.SHOP_ALREADY_BOUGHT, "%bw_lang_prefix%&cلقد اشتريت هذا بالفعل!");
        yml.addDefault(Messages.SHOP_ALREADY_HIGHER_TIER, "%bw_lang_prefix%&cلديك بالفعل عنصر بمستوى أعلى.");
        yml.addDefault(Messages.SHOP_UTILITY_NPC_SILVERFISH_NAME, "%bw_team_color%&l%bw_team_name% &r%bw_team_color%سلفرفيش");
        yml.addDefault(Messages.SHOP_UTILITY_NPC_IRON_GOLEM_NAME, "%bw_team_color%%bw_despawn_time%s &8[ %bw_team_color%%bw_health%&8]");
        yml.addDefault(Messages.SHOP_SEPARATOR_NAME, "&8⇧ الأقسام");
        yml.addDefault(Messages.SHOP_SEPARATOR_LORE, Collections.singletonList("&8⇩ العناصر"));
        yml.addDefault(Messages.SHOP_QUICK_BUY_NAME, "&bشراء سريع");
        yml.addDefault(Messages.SHOP_QUICK_BUY_LORE, new ArrayList<>());
        yml.addDefault(Messages.SHOP_QUICK_EMPTY_NAME, "&cفتحة فارغة!");
        yml.addDefault(Messages.SHOP_QUICK_EMPTY_LORE, Arrays.asList("&7هذه فتحة شراء سريع!", "&bانقر مع الانحناء &7لإضافة أي عنصر من", "&7المتجر إلى هنا."));
        yml.addDefault(Messages.SHOP_CAN_BUY_COLOR, "&a");
        yml.addDefault(Messages.SHOP_CANT_BUY_COLOR, "&c");
        yml.addDefault(Messages.SHOP_LORE_STATUS_CAN_BUY, "&eانقر للشراء!");
        yml.addDefault(Messages.SHOP_LORE_STATUS_CANT_AFFORD, "&cليس لديك ما يكفي من %bw_currency%!");
        yml.addDefault(Messages.SHOP_LORE_STATUS_MAXED, "&aمكتمل!");
        yml.addDefault(Messages.SHOP_LORE_STATUS_ARMOR, "&aتم التجهير!");
        yml.addDefault(Messages.SHOP_LORE_QUICK_ADD, "&bانقر مع الانحناء للإضافة للشراء السريع");
        yml.addDefault(Messages.SHOP_LORE_QUICK_REMOVE, "&bانقر مع الانحناء لإزالة من الشراء السريع!");
// الفئات في المتجر
        addCategoryMessages(yml, ConfigPath.SHOP_PATH_CATEGORY_BLOCKS, "&8كتل", "&aكتل", Collections.singletonList("&eانقر للمشاهدة!"));
        addCategoryMessages(yml, ConfigPath.SHOP_PATH_CATEGORY_MELEE, "&8الأسلحة البيضاء", "&aالأسلحة البيضاء", Collections.singletonList("&eانقر للمشاهدة!"));
        addCategoryMessages(yml, ConfigPath.SHOP_PATH_CATEGORY_ARMOR, "&8الدروع", "&aالدروع", Collections.singletonList("&eانقر للمشاهدة!"));
        addCategoryMessages(yml, ConfigPath.SHOP_PATH_CATEGORY_TOOLS, "&8الأدوات", "&aالأدوات", Collections.singletonList("&eانقر للمشاهدة!"));
        addCategoryMessages(yml, ConfigPath.SHOP_PATH_CATEGORY_RANGED, "&8الأسلحة بعيدة المدى", "&aالأسلحة بعيدة المدى", Collections.singletonList("&eانقر للمشاهدة!"));
        addCategoryMessages(yml, ConfigPath.SHOP_PATH_CATEGORY_POTIONS, "&8الجرعات", "&aالجرعات", Collections.singletonList("&eانقر للمشاهدة!"));
        addCategoryMessages(yml, ConfigPath.SHOP_PATH_CATEGORY_UTILITY, "&8الأدوات المساعدة", "&aالأدوات المساعدة", Collections.singletonList("&eانقر للمشاهدة!"));

// محتويات فئة الكتل
        addContentMessages(yml, "wool", ConfigPath.SHOP_PATH_CATEGORY_BLOCKS, "%bw_color%صوف", Arrays.asList("&7السعر: &f%bw_cost% %bw_currency%", "", "&7ممتاز لبناء الجسور بين الجزر.", "&7يتحول إلى لون فريقك.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "clay", ConfigPath.SHOP_PATH_CATEGORY_BLOCKS, "%bw_color%طين مقوى", Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "&7كتلة أساسية للدفاع عن سريرك.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "glass", ConfigPath.SHOP_PATH_CATEGORY_BLOCKS, "%bw_color%زجاج مقاوم للانفجار", Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "&7مقاوم للانفجارات.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "stone", ConfigPath.SHOP_PATH_CATEGORY_BLOCKS, "%bw_color%حجر النهاية", Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "&7كتلة صلبة للدفاع عن سريرك.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "ladder", ConfigPath.SHOP_PATH_CATEGORY_BLOCKS, "%bw_color%سلم", Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "&7مفيد لإنقاذ اللاعبين العالقين.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "obsidian", ConfigPath.SHOP_PATH_CATEGORY_BLOCKS, "%bw_color%أوبسيديان", Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "&7حماية قصوى لسريرك.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "wood", ConfigPath.SHOP_PATH_CATEGORY_BLOCKS, "%bw_color%خشب", Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "&7كتلة صلبة للدفاع عن سريرك.", "", "%bw_quick_buy%", "%bw_buy_status%"));

// محتويات فئة الأسلحة البيضاء
        addContentMessages(yml, "stone-sword", ConfigPath.SHOP_PATH_CATEGORY_MELEE, "%bw_color%سيف حجر", Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "iron-sword", ConfigPath.SHOP_PATH_CATEGORY_MELEE, "%bw_color%سيف حديد", Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "diamond-sword", ConfigPath.SHOP_PATH_CATEGORY_MELEE, "%bw_color%سيف ألماس", Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "stick", ConfigPath.SHOP_PATH_CATEGORY_MELEE, "%bw_color%عصا (دفع I)", Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "%bw_quick_buy%", "%bw_buy_status%"));

// محتويات فئة الدروع
        addContentMessages(yml, "chainmail", ConfigPath.SHOP_PATH_CATEGORY_ARMOR, "%bw_color%درع سلس دائم", Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "&7سروال وجزمة سلس ستظهر بها دائمًا.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "iron-armor", ConfigPath.SHOP_PATH_CATEGORY_ARMOR, "%bw_color%درع حديد دائم", Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "&7سروال وجزمة حديد ستظهر بها دائمًا.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "diamond-armor", ConfigPath.SHOP_PATH_CATEGORY_ARMOR, "%bw_color%درع ألماس دائم", Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "&7سروال وجزمة ألماس ستظهر بها دائمًا.", "", "%bw_quick_buy%", "%bw_buy_status%"));

// محتويات فئة الأدوات
        addContentMessages(yml, "shears", ConfigPath.SHOP_PATH_CATEGORY_TOOLS, "%bw_color%مقص دائم", Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "&7ممتاز لإزالة الصوف. ستظهر به دائمًا.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "pickaxe", ConfigPath.SHOP_PATH_CATEGORY_TOOLS, "%bw_color%فأس %bw_tier%", Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "&7المستوى: &e%bw_tier%", "", "&7عنصر قابل للترقية.", "&7يفقد مستوى عند الموت!", "", "&7ستظهر دائمًا على الأقل بالمستوى الأدنى.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "axe", ConfigPath.SHOP_PATH_CATEGORY_TOOLS, "%bw_color%فأس %bw_tier%", Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "&7المستوى: &e%bw_tier%", "", "&7عنصر قابل للترقية.", "&7يفقد مستوى عند الموت!", "", "&7ستظهر دائمًا على الأقل بالمستوى الأدنى.", "", "%bw_quick_buy%", "%bw_buy_status%"));

// محتويات فئة الأسلحة بعيدة المدى
        addContentMessages(yml, "arrow", ConfigPath.SHOP_PATH_CATEGORY_RANGED, "%bw_color%سهم", Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "bow1", ConfigPath.SHOP_PATH_CATEGORY_RANGED, "%bw_color%قوس", Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "bow2", ConfigPath.SHOP_PATH_CATEGORY_RANGED, "%bw_color%قوس (قوة I)", Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "bow3", ConfigPath.SHOP_PATH_CATEGORY_RANGED, "%bw_color%قوس (قوة I، دفع I)", Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "%bw_quick_buy%", "%bw_buy_status%"));

// محتويات فئة الجرعات
        addContentMessages(yml, "speed-potion", ConfigPath.SHOP_PATH_CATEGORY_POTIONS, "%bw_color%جرعة سرعة II (45 ثانية)", Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "jump-potion", ConfigPath.SHOP_PATH_CATEGORY_POTIONS, "%bw_color%جرعة قفز V (45 ثانية)", Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "invisibility", ConfigPath.SHOP_PATH_CATEGORY_POTIONS, "%bw_color%جرعة التخفي (30 ثانية)", Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "%bw_quick_buy%", "%bw_buy_status%"));

// محتويات فئة الأدوات المساعدة
        addContentMessages(yml, "golden-apple", ConfigPath.SHOP_PATH_CATEGORY_UTILITY, "%bw_color%تفاحة ذهبية", Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "&7شفاء ممتاز.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "bedbug", ConfigPath.SHOP_PATH_CATEGORY_UTILITY, "%bw_color%حشرة السرير", Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "&7ت spawn السيلفرفيش حيث", "&7تسقط كرة الثلج لتشتت أعدائك.", "&7تدوم 15 ثانية.", "", "%bw_quick_buy%", "%bw_buy_status%"));
// الأدوات والمرافق المساعدة
        addContentMessages(yml, "dream-defender", ConfigPath.SHOP_PATH_CATEGORY_UTILITY, "%bw_color%Dream Defender",
                Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "&7جولم حديدي لمساعدتك", "&7على الدفاع عن قاعدتك.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "fireball", ConfigPath.SHOP_PATH_CATEGORY_UTILITY, "%bw_color%كرة نارية",
                Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "&7انقر بالزر الأيمن لإطلاقها!", "&7رائع لدفع الأعداء عن الجسور الضيقة.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "tnt", ConfigPath.SHOP_PATH_CATEGORY_UTILITY, "%bw_color%TNT",
                Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "&7تشعل فورًا، مناسب لتفجير الأشياء!", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "ender-pearl", ConfigPath.SHOP_PATH_CATEGORY_UTILITY, "%bw_color%Ender Pearl",
                Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "&7أسرع طريقة لغزو قواعد الأعداء.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "water-bucket", ConfigPath.SHOP_PATH_CATEGORY_UTILITY, "%bw_color%دلو ماء",
                Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "&7ممتاز لإبطاء الأعداء.", "&7يمكنه حماية من TNT.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "bridge-egg", ConfigPath.SHOP_PATH_CATEGORY_UTILITY, "%bw_color%بيضة جسر",
                Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "&7تُنشئ جسرًا عند رميها.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "magic-milk", ConfigPath.SHOP_PATH_CATEGORY_UTILITY, "%bw_color%حليب سحري",
                Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "&7تجنب الفخاخ لمدة 60 ثانية بعد شربه.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "sponge", ConfigPath.SHOP_PATH_CATEGORY_UTILITY, "%bw_color%إسفنجة",
                Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "&7ممتازة لامتصاص المياه.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "tower", ConfigPath.SHOP_PATH_CATEGORY_UTILITY, "%bw_color%برج دفاعي صغير",
                Arrays.asList("&7السعر: %bw_cost% %bw_currency%", "", "&7ضع برجًا صغيرًا للدفاع!", "", "%bw_quick_buy%", "%bw_buy_status%"));

// الفخاخ
        yml.addDefault(Messages.MEANING_NO_TRAP, "لا يوجد فخ!");
        yml.addDefault(Messages.FORMAT_UPGRADE_TRAP_COST, "&7السعر: %bw_currency_color%%bw_cost% %bw_currency%");
        yml.addDefault(Messages.FORMAT_UPGRADE_COLOR_CAN_AFFORD, "&e");
        yml.addDefault(Messages.FORMAT_UPGRADE_COLOR_CANT_AFFORD, "&c");
        yml.addDefault(Messages.FORMAT_UPGRADE_COLOR_UNLOCKED, "&a");
        yml.addDefault(Messages.FORMAT_UPGRADE_TIER_LOCKED, "&7");
        yml.addDefault(Messages.FORMAT_UPGRADE_TIER_UNLOCKED, "&a");
        yml.addDefault(Messages.UPGRADES_LORE_REPLACEMENT_CLICK_TO_BUY, "%bw_color%انقر للشراء!");
        yml.addDefault(Messages.UPGRADES_LORE_REPLACEMENT_INSUFFICIENT_MONEY, "%bw_color%ليس لديك ما يكفي من %bw_currency%");
        yml.addDefault(Messages.UPGRADES_LORE_REPLACEMENT_INSUFFICIENT_SPACE, "&eلا يوجد مساحة كافية في حقيبتك!");
        yml.addDefault(Messages.UPGRADES_LORE_REPLACEMENT_LOCKED, "&cمقفل");
        yml.addDefault(Messages.UPGRADES_LORE_REPLACEMENT_UNLOCKED, "%bw_color%مفتوح");
        yml.addDefault(Messages.UPGRADES_UPGRADE_BOUGHT_CHAT, "&a%bw_player% اشترى &6%bw_upgrade_name%");
        yml.addDefault(Messages.UPGRADES_UPGRADE_ALREADY_CHAT, "&cلقد فتحت هذه الترقية مسبقًا!");
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("%bw_name%", "forge").replace("%bw_tier%", "tier-1"), "%bw_color%الفرن الحديدي");
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_LORE.replace("%bw_name%", "forge"),
                Arrays.asList("&7قم بترقية توليد الموارد على", "&7جزيرتك.", "", "{tier_1_color}المستوى 1: +50% موارد, &b{tier_1_cost} {tier_1_currency}",
                        "{tier_2_color}المستوى 2: +100% موارد, &b{tier_2_cost} {tier_2_currency}",
                        "{tier_3_color}المستوى 3: إنتاج الجمشت, &b{tier_3_cost} {tier_3_currency}",
                        "{tier_4_color}المستوى 4: +200% موارد, &b{tier_4_cost} {tier_4_currency}", ""));

        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("%bw_name%", "forge").replace("%bw_tier%", "tier-2"), "%bw_color%الفرن الذهبي");
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("%bw_name%", "forge").replace("%bw_tier%", "tier-3"), "%bw_color%فرن الجمشت");
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("%bw_name%", "forge").replace("%bw_tier%", "tier-4"), "%bw_color%الفرن المشتعل");

        yml.addDefault(Messages.UPGRADES_CATEGORY_ITEM_NAME_PATH + "traps", "&eشراء فخ");
        yml.addDefault(Messages.UPGRADES_CATEGORY_ITEM_LORE_PATH + "traps", Arrays.asList("&7سيتم", "&7وضع الفخاخ المشتراة في الطابور على اليمين.", "", "&eانقر للتصفح!"));

        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("%bw_name%", "swords").replace("%bw_tier%", "tier-1"), "%bw_color%سيوف مشحذة");
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_LORE.replace("%bw_name%", "swords"),
                Arrays.asList("&7يحصل فريقك بشكل دائم على", "&7حدة I على جميع السيوف والفؤوس!", "", "{tier_1_color}التكلفة: &b{tier_1_cost} {tier_1_currency}", ""));

        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("%bw_name%", "armor").replace("%bw_tier%", "tier-1"), "%bw_color%درع معزز I");
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_LORE.replace("%bw_name%", "armor"),
                Arrays.asList("&7يحصل فريقك بشكل دائم على", "&7حماية على جميع قطع الدرع!", "", "{tier_1_color}المستوى 1: حماية I, &b{tier_1_cost} {tier_1_currency}",
                        "{tier_2_color}المستوى 2: حماية II, &b{tier_2_cost} {tier_2_currency}",
                        "{tier_3_color}المستوى 3: حماية III, &b{tier_3_cost} {tier_3_currency}",
                        "{tier_4_color}المستوى 4: حماية IV, &b{tier_4_cost} {tier_4_currency}", ""));

        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("%bw_name%", "armor").replace("%bw_tier%", "tier-2"), "%bw_color%درع معزز II");
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("%bw_name%", "armor").replace("%bw_tier%", "tier-3"), "%bw_color%درع معزز III");
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("%bw_name%", "armor").replace("%bw_tier%", "tier-4"), "%bw_color%درع معزز IV");

        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("%bw_name%", "miner").replace("%bw_tier%", "tier-1"), "%bw_color%منقب مهووس I");
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_LORE.replace("%bw_name%", "miner"),
                Arrays.asList("&7يحصل جميع لاعبي فريقك", "&7على سرعة تعدين بشكل دائم.", "", "{tier_1_color}المستوى 1: سرعة تعدين I, &b{tier_1_cost} {tier_1_currency}",
                        "{tier_2_color}المستوى 2: سرعة تعدين II, &b{tier_2_cost} {tier_2_currency}", ""));
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("%bw_name%", "miner").replace("%bw_tier%", "tier-2"), "%bw_color%منقب مهووس II");

        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("%bw_name%", "heal-pool").replace("%bw_tier%", "tier-1"), "%bw_color%حوض الشفاء");
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_LORE.replace("%bw_name%", "heal-pool"),
                Arrays.asList("&7يخلق مجال تجدد", "&7حول قاعدتك!", "", "{tier_1_color}التكلفة: &b{tier_1_cost} {tier_1_currency}", ""));

        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("%bw_name%", "dragon").replace("%bw_tier%", "tier-1"), "%bw_color%تعزيز التنين");
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_LORE.replace("%bw_name%", "dragon"),
                Arrays.asList("&7سيحصل فريقك على تنينين", "&7بدلاً من تنين واحد أثناء الموت!", "", "{tier_1_color}التكلفة: &b{tier_1_cost} {tier_1_currency}", ""));

        yml.addDefault(Messages.UPGRADES_SEPARATOR_ITEM_NAME_PATH + "glass", "&8⬆&7قابل للشراء");
        yml.addDefault(Messages.UPGRADES_SEPARATOR_ITEM_LORE_PATH + "glass", Collections.singletonList("&8⬇&7طابور الفخاخ"));

        yml.addDefault(Messages.UPGRADES_TRAP_SLOT_ITEM_NAME_PATH + "first", "%bw_color%الفخ #1: %bw_name%");
        yml.addDefault(Messages.UPGRADES_TRAP_SLOT_ITEM_LORE1_PATH + "first", Arrays.asList("&7أول عدو يدخل", "&7قاعدتك سيُفعل", "&7هذا الفخ!"));
        yml.addDefault(Messages.UPGRADES_TRAP_SLOT_ITEM_LORE2_PATH + "first",
                Arrays.asList("", "&7شراء فخ سيضعه في الطابور هنا.", "&7ستزداد التكلفة بناءً على", "&7عدد الفخاخ في الطابور.", "", "&7الفخ القادم: &b%bw_cost% %bw_currency%"));

// نفس النمط يُكرر للفخ #2 والفخ #3
        yml.addDefault(Messages.UPGRADES_BASE_TRAP_ITEM_NAME_PATH + "1", "%bw_color%إنه فخ!");
        yml.addDefault(Messages.UPGRADES_BASE_TRAP_ITEM_LORE_PATH + "1", Arrays.asList("&7يسبب العمى وبطء الحركة", "&75 ثوانٍ.", ""));
        yml.addDefault(Messages.UPGRADES_BASE_TRAP_ITEM_NAME_PATH + "2", "%bw_color%فخ هجومي مضاد");
        yml.addDefault(Messages.UPGRADES_BASE_TRAP_ITEM_LORE_PATH + "2", Arrays.asList("&7يمنح سرعة I لمدة 15 ثانية", "&7للاعبين الحلفاء قرب القاعدة.", ""));
        yml.addDefault(Messages.UPGRADES_BASE_TRAP_ITEM_NAME_PATH + "3", "%bw_color%فخ الإنذار");
        yml.addDefault(Messages.UPGRADES_BASE_TRAP_ITEM_LORE_PATH + "3", Arrays.asList("&7يكشف اللاعبين المخفيين", "&7وأسمائهم وفريقهم.", ""));
        yml.addDefault(Messages.UPGRADES_BASE_TRAP_ITEM_NAME_PATH + "4", "%bw_color%فخ تعب المنقب");
        yml.addDefault(Messages.UPGRADES_BASE_TRAP_ITEM_LORE_PATH + "4", Arrays.asList("&7يسبب تعب التعدين لمدة 10", "&7ثوانٍ.", ""));

        yml.addDefault(Messages.UPGRADES_SEPARATOR_ITEM_NAME_PATH + "back", "&aعودة");
        yml.addDefault(Messages.UPGRADES_SEPARATOR_ITEM_LORE_PATH + "back", Collections.singletonList("&7إلى الترقيات والفخاخ"));
        yml.addDefault(Messages.UPGRADES_CATEGORY_GUI_NAME_PATH + "traps", "&8ضع فخاً في الطابور");
        yml.addDefault(Messages.UPGRADES_TRAP_QUEUE_LIMIT, "&cقائمة الفخاخ ممتلئة!");
        yml.addDefault(Messages.UPGRADES_TRAP_DEFAULT_MSG, "&c&lتم تفعيل %bw_trap%!");
        yml.addDefault(Messages.UPGRADES_TRAP_DEFAULT_TITLE, "&cفخ مفعل!");
        yml.addDefault(Messages.UPGRADES_TRAP_DEFAULT_SUBTITLE, "&fتم تفعيل %bw_trap% الخاص بك!");
        yml.addDefault(Messages.UPGRADES_TRAP_CUSTOM_MSG + "3", "&c&lتم تفعيل فخ الإنذار بواسطة &7&l%bw_player% &c&lمن فريق %bw_color%&l%bw_team%!");
        yml.addDefault(Messages.UPGRADES_TRAP_CUSTOM_TITLE + "3", "&c&lإنذار!!!");
        yml.addDefault(Messages.UPGRADES_TRAP_CUSTOM_SUBTITLE + "3", "&fتم تفعيل فخ الإنذار بواسطة فريق %bw_color%%bw_team%!");
        generateNPCMessages(yml,"default");
        save();
        setPrefix(m(Messages.PREFIX));
        setPrefixStatic(m(Messages.PREFIX));
    }
    @Override
    public void generateNPCMessages(YamlConfiguration yml, String group){
        yml.addDefault(Messages.NPC_NAME_TEAM_UPGRADES.replace("%group%", group), Arrays.asList("&bترقيات الفريق", "&e&lانقر بالزر الأيمن"));
        yml.addDefault(Messages.NPC_NAME_SOLO_UPGRADES.replace("%group%", group), Arrays.asList("&bترقيات الفردي", "&e&lانقر بالزر الأيمن"));
        yml.addDefault(Messages.NPC_NAME_TEAM_SHOP.replace("%group%", group), Arrays.asList("&bمتجر الفريق", "&e&lانقر بالزر الأيمن"));
        yml.addDefault(Messages.NPC_NAME_SOLO_SHOP.replace("%group%", group), Arrays.asList("&bمتجر العناصر", "&e&lانقر بالزر الأيمن"));
    }
}
