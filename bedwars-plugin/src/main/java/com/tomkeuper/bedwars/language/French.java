/*
 * BedWars2023 - A bed wars mini-game.
 * Copyright (C) 2024 Tomas Keuper
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact e-mail: contact@fyreblox.com
 */

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

public class French extends Language {

    public French() {
        super(BedWars.plugin, "fr");

        YamlConfiguration yml = getYml();
        yml.options().copyDefaults(true);
        yml.options().header("Le format RVB est supporté par 3 méthodes: <SOLID:FF0080> -> couleur unique, <RAINBOW1></RAINBOW> -> Arc en ciel (number customizable) & <GRADIENT:2C08BA></GRADIENT:028A97> -> Dégradé");
        yml.addDefault(Messages.PREFIX, "");
        yml.addDefault("name", "French");

        yml.addDefault(Messages.COMMAND_MAIN, Arrays.asList("", "&2▪ &7/" + BedWars.mainCmd + " stats", "&2▪ &7/" + BedWars.mainCmd + " join &o<arena/group>", "&2▪ &7/" + BedWars.mainCmd + " leave", "&2▪ &7/" + BedWars.mainCmd + " lang", "&2▪ &7/" + BedWars.mainCmd + " gui", "&2▪ &7/" + BedWars.mainCmd + " start &3(vip)"));
        yml.addDefault(Messages.COMMAND_LANG_LIST_HEADER, "%bw_lang_prefix% &2Langues disponibles:");
        yml.addDefault(Messages.COMMAND_LANG_LIST_FORMAT, "&a▪  &7%bw_lang_iso% - &f%bw_name%");
        yml.addDefault(Messages.COMMAND_LANG_USAGE, "%bw_lang_prefix%&7Utilisation: /lang &f&o<iso>");
        yml.addDefault(Messages.COMMAND_LANG_SELECTED_NOT_EXIST, "%bw_lang_prefix%&cCette langue n'existe pas!");
        yml.addDefault(Messages.COMMAND_LANG_SELECTED_SUCCESSFULLY, "%bw_lang_prefix%&aLangue changée!");
        yml.addDefault(Messages.COMMAND_LANG_USAGE_DENIED, "%bw_lang_prefix%&cVous ne pouvez pas changer la langue durant cette partie.");
        yml.addDefault(Messages.COMMAND_JOIN_USAGE, "&a▪ &7Utilisation: /" + BedWars.mainCmd + " join &o<arena/group>");
        yml.addDefault(Messages.COMMAND_JOIN_GROUP_OR_ARENA_NOT_FOUND, "%bw_lang_prefix%&cIl n'y a aucune arène disponible pour ce groupe: %bw_name%");
        yml.addDefault(Messages.COMMAND_JOIN_DENIED_IS_FULL, "%bw_lang_prefix%&cCette arène est pleine!\n&aVeuillez envisager de faire un don pour cette fonctionnalité. &7&o(click)");
        yml.addDefault(Messages.COMMAND_JOIN_NO_EMPTY_FOUND, "%bw_lang_prefix%&cIl n'y a aucune arène disponible maintenant );(");
        yml.addDefault(Messages.COMMAND_JOIN_DENIED_IS_FULL_OF_VIPS, "%bw_lang_prefix%&cNous regrettons mais cette arène est pleine.\n&cNous savons que vous êtes un donateur mais cette arène est pleine de personnel ou/et donateurs.");
        yml.addDefault(Messages.COMMAND_JOIN_DENIED_PARTY_TOO_BIG, "%bw_lang_prefix%&cVotre partie est trop grande pour rejoindre cette arène comme équipe :(");
        yml.addDefault(Messages.COMMAND_JOIN_DENIED_NOT_PARTY_LEADER, "%bw_lang_prefix%&cSeulement le chef de partie peut choisir une arène.");
        yml.addDefault(Messages.COMMAND_JOIN_PLAYER_JOIN_MSG, "%bw_lang_prefix%&7%bw_player% &ea rejoin (&b%bw_on%&e/&b%bw_max%&e)!");
        yml.addDefault(Messages.COMMAND_JOIN_SPECTATOR_MSG, "%bw_lang_prefix%&6Vous êtes maintenant spectateur &9%bw_arena%&6.\n%bw_lang_prefix%&eVous pouvez partir quand bon vous semble en faisant &c/leave&e.");
        yml.addDefault(Messages.COMMAND_JOIN_SPECTATOR_DENIED_MSG, "&cLes spectateurs ne sont pas autorisé dans cette arène!");
        yml.addDefault(Messages.COMMAND_TP_PLAYER_NOT_FOUND, "%bw_lang_prefix%&cJoueur non trouvé!");
        yml.addDefault(Messages.COMMAND_TP_NOT_IN_ARENA, "%bw_lang_prefix%&cCe joueur n'est pas dans une arène de bedwars!");
        yml.addDefault(Messages.COMMAND_TP_NOT_STARTED, "%bw_lang_prefix%&cL'arène ou le joueur se trouve n'a pas encore démarré!");
        yml.addDefault(Messages.COMMAND_TP_USAGE, "%bw_lang_prefix%&cUtilisation: /bw tp <Nom d'utilisateur>");
        yml.addDefault(Messages.REJOIN_NO_ARENA, "%bw_lang_prefix%&cIl n'y a pas d'arène à rejoindre!");
        yml.addDefault(Messages.REJOIN_DENIED, "%bw_lang_prefix%&cVous ne pouvez plus rejoindre cette arène. Partie finie ou lit détruit.");
        yml.addDefault(Messages.REJOIN_ALLOWED, "%bw_lang_prefix%&eEntrée dans l'arène &a%bw_arena%&e!");
        yml.addDefault(Messages.COMMAND_REJOIN_PLAYER_RECONNECTED, "%bw_lang_prefix%&7%bw_player% &ea déconnecté!");
        yml.addDefault(Messages.COMMAND_LEAVE_DENIED_NOT_IN_ARENA, "%bw_lang_prefix%&cVous n'êtes pas dans une arène!");
        yml.addDefault(Messages.COMMAND_LEAVE_MSG, "%bw_lang_prefix%&7%bw_player% &ea déconnecté!");
        yml.addDefault(Messages.COMMAND_LEAVE_HAS_PARTY_POPUP_TITLE, "Êtes-vous sûr ?");
        yml.addDefault(Messages.COMMAND_LEAVE_HAS_PARTY_POPUP_STAY, "&cNon");
        yml.addDefault(Messages.COMMAND_LEAVE_HAS_PARTY_POPUP_STAY_LORE, List.of("&fRester dans l'arène"));
        yml.addDefault(Messages.COMMAND_LEAVE_HAS_PARTY_POPUP_BRING_PARTY, "&aOui");
        yml.addDefault(Messages.COMMAND_LEAVE_HAS_PARTY_POPUP_BRING_PARTY_LORE, List.of("&fAmenez votre groupe avec vous"));
        yml.addDefault(Messages.COMMAND_NOT_ALLOWED_IN_GAME, "%bw_lang_prefix%&cVous ne pouvez pas faire ça durant la partie.");
        yml.addDefault(Messages.COMMAND_NOT_FOUND_OR_INSUFF_PERMS, "%bw_lang_prefix%&cCommande non trouvée ou vous n'avez pas la permission!");
        yml.addDefault(Messages.COMMAND_PARTY_HELP, Arrays.asList("&6▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                "&aCommande de Parties:",
                "&e/party help &7- &bAffiche ce message",
                "&e/party invite <Nom du joueur> &7- &bInvite un joueur à la partie",
                "&e/party leave &7- &bQuitte la partie",
                "&e/party remove <Nom du joueur> &7- &bRetire le joueur de la partie",
                "&e/party info &7- &bAffiche les membres de la partie",
                "&e/party promote <Nom du joueur> &7- &bChanger le chef de la partie",
                "&e/party accept <Nom du joueur> &7- &bAccepter une invitation de partie",
                "&e/party disband &7- &bDissoudre la partie")
        );
        yml.addDefault(Messages.COMMAND_PARTY_INVITE_USAGE, "%bw_lang_prefix%&eUtilisation: &7/party invite <Nom du joueur>");
        yml.addDefault(Messages.COMMAND_PARTY_INVITE_DENIED_PLAYER_OFFLINE, "%bw_lang_prefix%&7%bw_player% &en'est pas en ligne !");
        yml.addDefault(Messages.COMMAND_PARTY_INVITE_SENT, "%bw_lang_prefix%&eInvitation envoyé a &7%bw_player%&6.");
        yml.addDefault(Messages.COMMAND_PARTY_INVITE_SENT_TARGET_RECEIVE_MSG, "%bw_lang_prefix%&b%bw_player% &evous a invité dans sa partie! &o&7(Click pour accepter)");
        yml.addDefault(Messages.COMMAND_PARTY_INVITE_DENIED_CANNOT_INVITE_YOURSELF, "%bw_lang_prefix%&cVous ne pouvez pas vous inviter vous meme!");
        yml.addDefault(Messages.COMMAND_PARTY_ACCEPT_DENIED_NO_INVITE, "%bw_lang_prefix%&cIl n'y a pas d'invitation de parties a accepter!");
        yml.addDefault(Messages.COMMAND_PARTY_ACCEPT_DENIED_ALREADY_IN_PARTY, "%bw_lang_prefix%&eVous êtes déjà dans une partie!");
        yml.addDefault(Messages.COMMAND_PARTY_INSUFFICIENT_PERMISSIONS, "%bw_lang_prefix%&cSeulement le chef de la partie peut faire ca!");
        yml.addDefault(Messages.COMMAND_PARTY_ACCEPT_USAGE, "%bw_lang_prefix%&eUtilisation: &7/party accept <Nom du joueur>");
        yml.addDefault(Messages.COMMAND_PARTY_ACCEPT_SUCCESS, "%bw_lang_prefix%&7%bw_player% &ea rejoint la partie!");
        yml.addDefault(Messages.COMMAND_PARTY_GENERAL_DENIED_NOT_IN_PARTY, "%bw_lang_prefix%&cVous n'êtes pas dans une partie!");
        yml.addDefault(Messages.COMMAND_PARTY_LEAVE_DENIED_IS_OWNER_NEEDS_DISBAND, "%bw_lang_prefix%&cVous ne pouvez pas quitter votre propre partie!\n&eFaites plutôt: &b/party disband");
        yml.addDefault(Messages.COMMAND_PARTY_LEAVE_SUCCESS, "%bw_lang_prefix%&7%bw_player% &ea quitté la partie!");
        yml.addDefault(Messages.COMMAND_PARTY_DISBAND_SUCCESS, "%bw_lang_prefix%&ePartie dissoute!");
        yml.addDefault(Messages.COMMAND_PARTY_REMOVE_USAGE, "%bw_lang_prefix%&7Utilisation: &e/party remove <Nom du joueur>");
        yml.addDefault(Messages.COMMAND_PARTY_REMOVE_SUCCESS, "%bw_lang_prefix%&7%bw_player% &ea été retiré de la partie,");
        yml.addDefault(Messages.COMMAND_PARTY_REMOVE_DENIED_TARGET_NOT_PARTY_MEMBER, "%bw_lang_prefix%&7%bw_player% &en'est pas dans votre partie!");
        yml.addDefault(Messages.COMMAND_PARTY_PROMOTE_SUCCESS, "%bw_lang_prefix%&eVous avez promu %bw_player% avec succès au rang de chef");
        yml.addDefault(Messages.COMMAND_PARTY_PROMOTE_OWNER, "%bw_lang_prefix%&eVous avez été promu au rang de chef de partie");
        yml.addDefault(Messages.COMMAND_PARTY_PROMOTE_NEW_OWNER, "%bw_lang_prefix%&7 &e%bw_player% a été promu chef de partie");
        yml.addDefault(Messages.COMMAND_PARTY_INFO_OWNER, "\n%bw_lang_prefix%&eLe chef de la partie est: &7%bw_party_owner%");
        yml.addDefault(Messages.COMMAND_PARTY_INFO_PLAYERS, "%bw_lang_prefix%&eMembres de la partie:");
        yml.addDefault(Messages.COMMAND_PARTY_INFO_PLAYER, "&7%bw_player%");
        yml.addDefault(Messages.COMMAND_PARTY_CHAT_ENABLED_DISABLED, "&e&lPARTIE &8&l┃ &fLe chat de partie est maintenant %bw_party_chat_status% &f!");
        yml.addDefault(Messages.COMMAND_FORCESTART_NOT_IN_GAME, "&c▪ &7Vous n'êtes pas entrain de jouer!");
        yml.addDefault(Messages.COMMAND_FORCESTART_SUCCESS, "&c▪ &7compte à rebours raccourci!");
        yml.addDefault(Messages.COMMAND_FORCESTART_NO_PERM, "%bw_lang_prefix%&7Vous ne pouvez pas démarrer de force l'arène.\n&7Veuillez considerer la donation pour acceder au fonctionnalités VIP.");
        yml.addDefault(Messages.COMMAND_COOLDOWN, "&cVous ne pouvez pas encore faire ca! Attendez encore %bw_seconds% secondes!");
        yml.addDefault(Messages.COMMAND_SHOUT_DISABLE_SOLO, "&cLe cri est désactivé en solo !");
        yml.addDefault(Messages.COMMAND_LEAVE_STARTED, "&a&lTéléportation au lobby dans %bw_leave_delay% secondes... Click-droit pour annuler!");
        yml.addDefault(Messages.COMMAND_LEAVE_CANCELED, "&c&lTéléportation annulée!");
        yml.addDefault(Messages.ARENA_JOIN_VIP_KICK, "%bw_lang_prefix%&cDésolé, mais vous avez été déconnecté car un donateur a rejoin l'arène car un donateur a rejoint l'arène\n&aVeuillez considérer la donation pour plus de fonctionnalités. &7&o(click)");
        yml.addDefault(Messages.ARENA_START_COUNTDOWN_STOPPED_INSUFF_PLAYERS_CHAT, "%bw_lang_prefix%&cIl n'y a pas suffisamment de joueurs! Compte à rebours arrêté!");
        yml.addDefault(Messages.ARENA_RESTART_PLAYER_KICK, "%bw_lang_prefix%&eL'arène ou vous vous trouvez redémarre.");
        yml.addDefault(Messages.ARENA_STATUS_PLAYING_NAME, "&cEn jeu");
        yml.addDefault(Messages.ARENA_STATUS_RESTARTING_NAME, "&4Redémarrage");
        yml.addDefault(Messages.ARENA_STATUS_WAITING_NAME, "&2Attente &c%bw_full%");
        yml.addDefault(Messages.ARENA_STATUS_STARTING_NAME, "&6Démarrage &c%bw_full%");
        yml.addDefault(Messages.ARENA_GUI_INV_NAME, "&8Click pour rejoindre");
        yml.addDefault(Messages.ARENA_GUI_ARENA_CONTENT_NAME, "&a&l%bw_name%");
        yml.addDefault(Messages.ARENA_GUI_ARENA_CONTENT_LORE, Arrays.asList("", "&7Status: %bw_arena_status%", "&7Joueurs: &f%bw_on%&7/&f%bw_max%", "&7Type: &a%bw_group%", "", "&aClick-Gauche pour rejoindre.", "&eClick-droit pour regarder."));
        yml.addDefault(Messages.ARENA_GUI_SKIPPED_ITEM_NAME, "&r%bw_server_ip%");
        yml.addDefault(Messages.ARENA_GUI_SKIPPED_ITEM_LORE, Collections.emptyList());
        yml.addDefault(Messages.ARENA_STATUS_START_COUNTDOWN_CHAT, "%bw_lang_prefix%&eLa partie démarre dans &6%bw_time% &esecondes!");
        yml.addDefault(Messages.ARENA_STATUS_START_COUNTDOWN_TITLE, " ");
        yml.addDefault(Messages.ARENA_STATUS_START_COUNTDOWN_SUB_TITLE, "&a%bw_seconds%");
        yml.addDefault(Messages.ARENA_STATUS_START_COUNTDOWN_SUB_TITLE + "-5", "&e❺");
        yml.addDefault(Messages.ARENA_STATUS_START_COUNTDOWN_SUB_TITLE + "-4", "&e❹");
        yml.addDefault(Messages.ARENA_STATUS_START_COUNTDOWN_SUB_TITLE + "-3", "&c❸");
        yml.addDefault(Messages.ARENA_STATUS_START_COUNTDOWN_SUB_TITLE + "-2", "&c❷");
        yml.addDefault(Messages.ARENA_STATUS_START_COUNTDOWN_SUB_TITLE + "-1", "&c❶");
        yml.addDefault(Messages.ARENA_STATUS_START_COUNTDOWN_CANCELLED_TITLE, " ");
        yml.addDefault(Messages.ARENA_STATUS_START_COUNTDOWN_CANCELLED_SUB_TITLE, "&cEn attente de plus de joueurs..");
        yml.addDefault(Messages.ARENA_STATUS_START_PLAYER_TITLE, "&aGO");
        yml.addDefault(Messages.ARENA_STATUS_START_PLAYER_TUTORIAL, Arrays.asList(
                "&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                "&f                                   &lBedWars", "",
                "&e&l    Protège ton lit et détruit celui des ennemis.",
                "&e&l      Améliore toi et ton équipe en collectant",
                "&e&l   Fer, Or, Émeraudes, et Diamants depuis les générateurs",
                "&e&l             pour acceder a des améliorations puissantes.", "",
                "&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
        yml.addDefault(Messages.ARENA_JOIN_DENIED_SELECTOR, "%bw_lang_prefix%&cDésolé mais vous ne pouvez pas rejoindre cette arène maintenant. Click-droit pour regarder!");
        yml.addDefault(Messages.ARENA_SPECTATE_DENIED_SELECTOR, "%bw_lang_prefix%&cDésolé mais vous ne pouvez pas regarder cette arène maintenant. Click-gauche pour rejoindre!");
        yml.addDefault(Messages.ARENA_JOIN_DENIED_NO_PROXY, "&cDésole mais vous devez rejoindre cette arène en utilisant BWProxy2023. \n&eSi vous voulez configurer une arène donnez vous la permission bw.setup pour rejoindre le serveur directement!");
        yml.addDefault(Messages.ARENA_JOIN_DENIED_NO_TIME, "&cDésole mais vous avez rejoin quand la partie était déjà démarrée.");
        yml.addDefault(Messages.ARENA_SPECTATOR_TELEPORTER_GUI_NAME, "&8Téléporteur");
        yml.addDefault(Messages.ARENA_SPECTATOR_TELEPORTER_GUI_HEAD_NAME, "%bw_v_prefix%%bw_player%");
        yml.addDefault(Messages.ARENA_SPECTATOR_TELEPORTER_GUI_HEAD_LORE, Arrays.asList("&7Vie: &f%bw_player_health%%", "&7Nourriture: &f%bw_player_food%", "", "&7Click-gauche pour regarder"));
        yml.addDefault(Messages.ARENA_SPECTATOR_LEAVE_ITEM_NAME, "&c&lRetourner au lobby");
        yml.addDefault(Messages.ARENA_SPECTATOR_LEAVE_ITEM_LORE, Collections.singletonList("&7Click-droit pour quitter et retourner au lobby!"));
        yml.addDefault(Messages.ARENA_SPECTATOR_FIRST_PERSON_ENTER_TITLE, "&aRegarder &7%bw_player%");
        yml.addDefault(Messages.ARENA_SPECTATOR_FIRST_PERSON_ENTER_SUBTITLE, "&cACCROUPIR pour quitter");
        yml.addDefault(Messages.ARENA_SPECTATOR_FIRST_PERSON_LEAVE_TITLE, "&eSortie du mode spectateur");
        yml.addDefault(Messages.ARENA_SPECTATOR_FIRST_PERSON_LEAVE_SUBTITLE, "");
        yml.addDefault(Messages.ARENA_LEAVE_PARTY_DISBANDED, "%bw_lang_prefix%&cLe chef de la partie a quitté la partie et est donc dissoute!");
        yml.addDefault(Messages.GENERATOR_HOLOGRAM_TIER, "&eNiveau &c%bw_tier%");
        yml.addDefault(Messages.GENERATOR_HOLOGRAM_TYPE_DIAMOND, "&b&lDiamant");
        yml.addDefault(Messages.GENERATOR_HOLOGRAM_TYPE_EMERALD, "&a&lÉmeraude");
        yml.addDefault(Messages.GENERATOR_HOLOGRAM_TIMER, "&eApparaît dans &c%bw_seconds% &esecondes");
        yml.addDefault(Messages.GENERATOR_UPGRADE_CHAT_ANNOUNCEMENT, "%bw_lang_prefix% Les générateurs d'%bw_generator_type% &eont été améliorés au Niveau &c%bw_tier%");
        yml.addDefault(Messages.FORMATTING_CHAT_LOBBY, "%bw_level%%bw_v_prefix%&7%bw_player%%bw_v_suffix%: %bw_message%");
        yml.addDefault(Messages.FORMATTING_CHAT_WAITING, "%bw_level%%bw_v_prefix%&7%bw_player%%bw_v_suffix%: %bw_message%");
        yml.addDefault(Messages.FORMATTING_CHAT_SHOUT, "%bw_level%%bw_v_prefix%&6[CRI] %bw_team_format% &7%bw_player%&f%bw_v_suffix%: %bw_message%");
        yml.addDefault(Messages.FORMATTING_CHAT_TEAM, "%bw_level%%bw_v_prefix%&f%bw_team_format%&7 %bw_player%%bw_v_suffix%: %bw_message%");
        yml.addDefault(Messages.FORMATTING_CHAT_SPECTATOR, "%bw_level%%bw_v_prefix%&7[SPECTATEUR] %bw_player%%bw_v_suffix%: %bw_message%");
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

        yml.addDefault(Messages.FORMATTING_SCOREBOARD_DATE, "jj/MM/AA");
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_TEAM_GENERIC, "%bw_team_color%%bw_team_letter%&f %bw_team_name%: %bw_team_status%");
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_TEAM_ELIMINATED, "&c&l✘");
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_BED_DESTROYED, "&a%bw_players_remaining%");
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_TEAM_ALIVE, "&a&l✓");
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_NEXEVENT_TIMER, "mm:ss");
        yml.addDefault(Messages.FORMATTING_SCOREBOARD_YOUR_TEAM, "&7 YOU");
        yml.addDefault(Messages.FORMATTING_ACTION_BAR_TRACKING, "&fSuivi: %bw_team% &f- Distance: %bw_distance%m");
        yml.addDefault(Messages.FORMATTING_BOSSBAR_DRAGON, "%bw_team% &7Dragon");
        yml.addDefault(Messages.FORMATTING_TEAM_WINNER_FORMAT, "      %bw_team_color%%bw_team_name% &7- %bw_winner_members%");
        yml.addDefault(Messages.FORMATTING_SOLO_WINNER_FORMAT, "                 %bw_team_color%%bw_team_name% &7- %bw_winner_members%");
        yml.addDefault(Messages.FORMATTING_GENERATOR_TIER1, "I");
        yml.addDefault(Messages.FORMATTING_GENERATOR_TIER2, "II");
        yml.addDefault(Messages.FORMATTING_GENERATOR_TIER3, "III");
        yml.addDefault(Messages.FORMATTING_DESPAWNABLE_UTILITY_NPC_HEALTH, "▮ ");
        yml.addDefault(Messages.FORMATTING_STATS_DATE_FORMAT, "aaaa/MM/jj HH:mm");
        yml.addDefault(Messages.FORMAT_PAPI_PLAYER_TEAM_TEAM, "%bw_team_color%[%bw_team_name%]");
        yml.addDefault(Messages.FORMAT_PAPI_PLAYER_TEAM_SHOUT, "&6[CRI]");
        yml.addDefault(Messages.FORMAT_PAPI_PLAYER_TEAM_SPECTATOR, "&7[SPECTATEUR]");
        yml.addDefault(Messages.MEANING_FULL, "Plein");
        yml.addDefault(Messages.MEANING_SHOUT, "cri");
        yml.addDefault(Messages.MEANING_NOBODY, "personne");
        yml.addDefault(Messages.MEANING_NEVER, "jamais");
        yml.addDefault(Messages.MEANING_IRON_SINGULAR, "Fer");
        yml.addDefault(Messages.MEANING_IRON_PLURAL, "Fer");
        yml.addDefault(Messages.MEANING_GOLD_SINGULAR, "Or");
        yml.addDefault(Messages.MEANING_GOLD_PLURAL, "Or");
        yml.addDefault(Messages.MEANING_EMERALD_SINGULAR, "Émeraude");
        yml.addDefault(Messages.MEANING_EMERALD_PLURAL, "Émeraude");
        yml.addDefault(Messages.MEANING_DIAMOND_SINGULAR, "Diamant");
        yml.addDefault(Messages.MEANING_DIAMOND_PLURAL, "Diamant");
        yml.addDefault(Messages.MEANING_VAULT_SINGULAR, "$");
        yml.addDefault(Messages.MEANING_VAULT_PLURAL, "$");
        yml.addDefault(Messages.MEANING_ENABLED, "&aActivé");
        yml.addDefault(Messages.MEANING_DISABLED, "&cDésactivé");
        yml.addDefault(Messages.INTERACT_CANNOT_PLACE_BLOCK, "%bw_lang_prefix%&cVous ne pouvez pas placer de bloc ici!");
        yml.addDefault(Messages.INTERACT_CANNOT_BREAK_BLOCK, "%bw_lang_prefix%&cVous pouvez casser uniquement les blocs posé par un joueur!");
        yml.addDefault(Messages.INTERACT_FULL_CHEST, "%bw_lang_prefix%&cLe coffre est plein!");
        yml.addDefault(Messages.INTERACT_CANNOT_BREAK_OWN_BED, "&cVous ne pouvez pas casser votre propre lit!");
        yml.addDefault(Messages.INTERACT_BED_DESTROY_CHAT_ANNOUNCEMENT, "\n&f&lDESTRUCTION DE LIT > %bw_team_color%%bw_team_name% Lit &7a été frit par %bw_player_color%%bw_player%&7!\n");
        yml.addDefault(Messages.INTERACT_BED_DESTROY_TITLE_ANNOUNCEMENT, "&cDESTRUCTION DE LIT!");
        yml.addDefault(Messages.INTERACT_BED_DESTROY_SUBTITLE_ANNOUNCEMENT, "&fVous ne pourrez plus réapparaître!");
        yml.addDefault(Messages.INTERACT_BED_DESTROY_CHAT_ANNOUNCEMENT_TO_VICTIM, "&f&lDESTRUCTION DE LIT > &7Votre lit a été glacé par %bw_player_color%%bw_player%&7!");
        yml.addDefault(Messages.INTERACT_CHEST_CANT_OPEN_TEAM_ELIMINATED, "&cVous ne pouvez pas ouvrir le coffre car l'équipe n'est pas éliminée!");
        yml.addDefault(Messages.INTERACT_INVISIBILITY_REMOVED_DAMGE_TAKEN, "&cVous n'êtes plus invincible car vous avez pris des dégâts!");
        yml.addDefault(Messages.INTERACT_MAGIC_MILK_REMOVED, "&cVotre lait magique est périmé!");
        yml.addDefault(Messages.EGGBRIDGE_BUILD_LIMIT_WARNING, "&cVous êtes trop proche de la limite de construction!");
        yml.addDefault(Messages.PLAYER_DIE_VOID_FALL_REGULAR_KILL, "%bw_player_color%%bw_player% &7est tombé dans le vide.");
        yml.addDefault(Messages.PLAYER_DIE_VOID_FALL_FINAL_KILL, "%bw_player_color%%bw_player% &7est tombé dans le vide. &b&lMORT DÉFINITIVE!");
        yml.addDefault(Messages.PLAYER_DIE_KNOCKED_IN_VOID_REGULAR_KILL, "%bw_player_color%%bw_player% &7s'est fait pousser dans le vide par %bw_killer_color%%bw_killer_name%&7.");
        yml.addDefault(Messages.PLAYER_DIE_KNOCKED_IN_VOID_FINAL_KILL, "%bw_player_color%%bw_player% &7s'est fait pousser dans le vide par %bw_killer_color%%bw_killer_name%&7. &b&lMORT DÉFINITIVE!");
        yml.addDefault(Messages.PLAYER_DIE_PVP_LOG_OUT_REGULAR, "%bw_player_color%%bw_player% &7s'est déconnecté lors d'un combat avec %bw_killer_color%%bw_killer_name%&7.");
        yml.addDefault(Messages.PLAYER_DIE_PVP_LOG_OUT_FINAL, "%bw_player_color%%bw_player% &7s'est déconnecté lors d'un combat avec %bw_killer_color%%bw_killer_name%&7. &b&lMORT DÉFINITIVE!");
        yml.addDefault(Messages.PLAYER_DIE_KNOCKED_BY_REGULAR_KILL, "%bw_player_color%%bw_player% &7a été poussé par %bw_killer_color%%bw_killer_name%&7.");
        yml.addDefault(Messages.PLAYER_DIE_KNOCKED_BY_FINAL_KILL, "%bw_player_color%%bw_player% &7a été poussé par %bw_killer_color%%bw_killer_name%&7. &b&lMORT DÉFINITIVE!");
        yml.addDefault(Messages.PLAYER_DIE_EXPLOSION_WITH_SOURCE_REGULAR_KILL, "%bw_player_color%%bw_player% &7a été frappé par une bombe d'amour de %bw_killer_color%%bw_killer_name%&7.");
        yml.addDefault(Messages.PLAYER_DIE_EXPLOSION_WITH_SOURCE_FINAL_KILL, "%bw_player_color%%bw_player% &7a été frappé par une bombe d'amour de %bw_killer_color%%bw_killer_name%&7. &b&lMORT DÉFINITIVE!");
        yml.addDefault(Messages.PLAYER_DIE_EXPLOSION_WITHOUT_SOURCE_REGULAR, "%bw_player_color%%bw_player% &7a été touché par une bombe.");
        yml.addDefault(Messages.PLAYER_DIE_EXPLOSION_WITHOUT_SOURCE_FINAL_KILL, "%bw_player_color%%bw_player% &7a été touché par une bombe. &b&lMORT DÉFINITIVE!");
        yml.addDefault(Messages.PLAYER_DIE_PVP_REGULAR_KILL, "%bw_player_color%%bw_player% &7a été tué par %bw_killer_color%%bw_killer_name%&7.");
        yml.addDefault(Messages.PLAYER_DIE_PVP_FINAL_KILL, "%bw_player_color%%bw_player% &7a été tué par %bw_killer_color%%bw_killer_name%&7. &b&lMORT DÉFINITIVE!");
        yml.addDefault(Messages.PLAYER_DIE_UNKNOWN_REASON_REGULAR, "%bw_player_color%%bw_player% &7died.");
        yml.addDefault(Messages.PLAYER_DIE_UNKNOWN_REASON_FINAL_KILL, "%bw_player_color%%bw_player% &7décédé. &b&lMORT DÉFINITIVE!");
        yml.addDefault(Messages.PLAYER_DIE_SHOOT_REGULAR, "%bw_player_color%%bw_player% &7a été abattu par %bw_killer_color%%bw_killer_name%&7!");
        yml.addDefault(Messages.PLAYER_DIE_SHOOT_FINAL_KILL, "%bw_player_color%%bw_player% &7a été abattu par %bw_killer_color%%bw_killer_name%&7! &b&lMORT DÉFINITIVE!");
        yml.addDefault(Messages.PLAYER_DIE_DEBUG_REGULAR, "%bw_player_color%%bw_player% &7a été tué par le moucheron de %bw_killer_color%%bw_killer_team_name%!");
        yml.addDefault(Messages.PLAYER_DIE_DEBUG_FINAL_KILL, "%bw_player_color%%bw_player% &7a été tué par le moucheron de %bw_killer_color%%bw_killer_team_name%! &b&lMORT DÉFINITIVE!");
        yml.addDefault(Messages.PLAYER_DIE_IRON_GOLEM_REGULAR, "%bw_player_color%%bw_player% &7a été tué par le golem de fer de %bw_killer_color%%bw_killer_team_name%!");
        yml.addDefault(Messages.PLAYER_DIE_IRON_GOLEM_FINAL_KILL, "%bw_player_color%%bw_player% &7a été tué par le golem de fer de %bw_killer_color%%bw_killer_team_name%! &b&lMORT DÉFINITIVE!");
        yml.addDefault(Messages.PLAYER_DIE_REWARD_DIAMOND, "%bw_lang_prefix%&b+%bw_amount% %bw_meaning%");
        yml.addDefault(Messages.PLAYER_DIE_REWARD_EMERALD, "%bw_lang_prefix%&a+%bw_amount% %bw_meaning%");
        yml.addDefault(Messages.PLAYER_DIE_REWARD_IRON, "%bw_lang_prefix%&f+%bw_amount% %bw_meaning%");
        yml.addDefault(Messages.PLAYER_DIE_REWARD_GOLD, "%bw_lang_prefix%&6+%bw_amount% %bw_meaning%");
        yml.addDefault(Messages.ARENA_MAX_BUILD_LIMIT_REACHED, "&cLimite de construction haute atteinte!");
        yml.addDefault(Messages.ARENA_MIN_BUILD_LIMIT_REACHED, "&cLimite de construction basse atteinte!");
        yml.addDefault(Messages.ARENA_FIREBALL_COOLDOWN, "&cVeuillez attendre %bw_cooldown%s pour réutiliser cella!");
        yml.addDefault(Messages.ARENA_IN_GAME_ANNOUNCEMENT, Arrays.asList("&c&lSi vous êtes déconnecté utiliser /rejoin pour rejoindre la partie.", "&c&lL'arrangement entre équipe n'est pas permis! Pour les signaler utiliser /report."));
        yml.addDefault(Messages.PLAYER_DIE_RESPAWN_TITLE, "&cVOUS ÊTES MORT!");
        yml.addDefault(Messages.PLAYER_DIE_RESPAWN_SUBTITLE, "&eVous allez réapparaitre dans &c%bw_time% &esecondes!");
        yml.addDefault(Messages.PLAYER_DIE_RESPAWN_CHAT, "%bw_lang_prefix%&eVous allez réapparaitre dans &c%bw_time% &esecondes!");
        yml.addDefault(Messages.PLAYER_DIE_RESPAWNED_TITLE, "&aRÉAPPARU!");
        yml.addDefault(Messages.PLAYER_DIE_ELIMINATED_CHAT, "%bw_lang_prefix%&cVous avez été éliminé!");
        yml.addDefault(Messages.PLAYER_DIE_RESPAWNED_TEXT, "%bw_lang_prefix%&eVous avez réapparu!");
        yml.addDefault(Messages.PLAYER_HIT_BOW, "%bw_lang_prefix%%bw_player% &7a &c%bw_damage_amount% &7PV!");
        yml.addDefault(Messages.GAME_END_GAME_OVER_PLAYER_TITLE, "&c&lJEU TERMINÉ!");
        yml.addDefault(Messages.GAME_END_VICTORY_PLAYER_TITLE, "&6&lVICTOIRE!");
        yml.addDefault(Messages.GAME_END_TEAM_WON_CHAT, "%bw_lang_prefix%%bw_team_color%%bw_team_name% &aa gagné la partie!");
        yml.addDefault(Messages.GAME_END_NO_WINNERS, "&cPas de gagnants !");
        yml.addDefault(Messages.FORMATTING_EACH_WINNER, "%bw_player%");
        yml.addDefault(Messages.GAME_END_FIRST_KILLER, "%bw_player%");
        yml.addDefault(Messages.GAME_END_SECOND_KILLER, "%bw_player%");
        yml.addDefault(Messages.GAME_END_THIRD_KILLER, "%bw_player%");
        yml.addDefault(Messages.GAME_END_TOP_PLAYER_CHAT, Arrays.asList(
                "&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                "&f                                   &lBedWars", "", "%bw_winner_format%", "", "",
                "&e                          &l1er Tueur &7- %bw_first_format% - %bw_first_kills%",
                "&6                          &l2er Tueur &7- %bw_second_format% - %bw_second_kills%",
                "&c                          &l3er Tueur &7- %bw_third_format% - %bw_third_kills%", "",
                "&a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
        yml.addDefault(Messages.BED_HOLOGRAM_DEFEND, "&c&lDéfendez votre lit!");
        yml.addDefault(Messages.BED_HOLOGRAM_DESTROYED, "&c&lVotre lit a été détruit!");
        yml.addDefault(Messages.TEAM_ELIMINATED_CHAT, "\n&f&lÉQUIPE ÉLIMINÉ > L'équipe %bw_team_color%%bw_team_name% &ca été éliminé!\n");
        yml.addDefault(Messages.NEXT_EVENT_BEDS_DESTROY, "&fLITS DISPARUS");
        yml.addDefault(Messages.NEXT_EVENT_DIAMOND_UPGRADE_II, "&fDiamant II");
        yml.addDefault(Messages.NEXT_EVENT_DIAMOND_UPGRADE_III, "&fDiamant III");
        yml.addDefault(Messages.NEXT_EVENT_DRAGON_SPAWN, "&fMort subite");
        yml.addDefault(Messages.NEXT_EVENT_EMERALD_UPGRADE_II, "&fÉmeraude II");
        yml.addDefault(Messages.NEXT_EVENT_EMERALD_UPGRADE_III, "&fÉmeraude III");
        yml.addDefault(Messages.NEXT_EVENT_GAME_END, "&4Fin de partie");
        yml.addDefault(Messages.NEXT_EVENT_TITLE_ANNOUNCE_BEDS_DESTROYED, "&cLIT DÉTRUIT!");
        yml.addDefault(Messages.NEXT_EVENT_SUBTITLE_ANNOUNCE_BEDS_DESTROYED, "&fTous les lits ont été détruits!");
        yml.addDefault(Messages.NEXT_EVENT_CHAT_ANNOUNCE_BEDS_DESTROYED, "&c&lTous les lits ont été détruits!");
        yml.addDefault(Messages.NEXT_EVENT_TITLE_ANNOUNCE_SUDDEN_DEATH, "&cMort subite");
        yml.addDefault(Messages.NEXT_EVENT_SUBTITLE_ANNOUNCE_SUDDEN_DEATH, "");
        yml.addDefault(Messages.NEXT_EVENT_CHAT_ANNOUNCE_SUDDEN_DEATH, "&cMORT SUBITE: &6&b%bw_dragons_amount% Dragon %bw_team_color%%bw_team_name%!");
        yml.addDefault(Messages.XP_REWARD_PER_MINUTE, "%bw_lang_prefix%&6+%bw_xp% Expérience BedWars reçue (Temps de jeu).");
        yml.addDefault(Messages.XP_REWARD_WIN, "%bw_lang_prefix%&6+%bw_xp% Expérience BedWars reçue (Partie gagnée).");
        yml.addDefault(Messages.XP_REWARD_PER_TEAMMATE, "%bw_lang_prefix%&6+%bw_xp% Expérience BedWars reçue (Support d'équipe).");
        yml.addDefault(Messages.XP_REWARD_BED_DESTROY, "%bw_lang_prefix%&6+%bw_xp% Expérience BedWars reçue (Lit Détruit).");
        yml.addDefault(Messages.XP_REWARD_REGULAR_KILL, "%bw_lang_prefix%&6+%bw_xp% Expérience BedWars reçue (Kill régulier).");
        yml.addDefault(Messages.XP_REWARD_FINAL_KILL, "%bw_lang_prefix%&6+%bw_xp% Expérience BedWars reçue (MORT DÉFINITIVE).");
        yml.addDefault(Messages.XP_REWARD_HALLOWEEN, "%bw_lang_prefix%&6+5 Expérience BedWars reçue (Halloween).");
        yml.addDefault(Messages.PLAYER_LEVEL_UP, Collections.singletonList("&aFélicitations! Vous passez au niveau supérieur %bw_level%."));

        yml.addDefault(Messages.MONEY_REWARD_PER_MINUTE, "%bw_lang_prefix%&6+%bw_money% Pièces (Temps de jeu).");
        yml.addDefault(Messages.MONEY_REWARD_WIN, "%bw_lang_prefix%&6+%bw_money% Pièces (Victoire).");
        yml.addDefault(Messages.MONEY_REWARD_PER_TEAMMATE, "%bw_lang_prefix%&6+%bw_money% Pièces (Support d'équipe).");
        yml.addDefault(Messages.MONEY_REWARD_BED_DESTROYED, "%bw_lang_prefix%&6+%bw_money% Pièces (Lit détruit).");
        yml.addDefault(Messages.MONEY_REWARD_FINAL_KILL, "%bw_lang_prefix%&6+%bw_money% Pièces (MORT DÉFINITIVE).");
        yml.addDefault(Messages.MONEY_REWARD_REGULAR_KILL, "%bw_lang_prefix%&6+%bw_money% Pièces (Kill régulier).");

        yml.addDefault(Messages.HALLOWEEN_ITEM_NAME, "&6&lJoyeux Halloween");

        /* Lobby Command Items */
        yml.addDefault(Messages.GENERAL_CONFIGURATION_LOBBY_ITEMS_NAME.replace("%path%", "stats"), "&eStats");
        yml.addDefault(Messages.GENERAL_CONFIGURATION_LOBBY_ITEMS_LORE.replace("%path%", "stats"), Collections.singletonList("&fClick-gauche pour voir vos stats!"));
        yml.addDefault(Messages.GENERAL_CONFIGURATION_LOBBY_ITEMS_NAME.replace("%path%", "arena-selector"), "&eSélecteur d'arène");
        yml.addDefault(Messages.GENERAL_CONFIGURATION_LOBBY_ITEMS_LORE.replace("%path%", "arena-selector"), Collections.singletonList("&fClick-gauche pour choisir une arène!"));
        yml.addDefault(Messages.GENERAL_CONFIGURATION_LOBBY_ITEMS_NAME.replace("%path%", "leave"), "&eRetour au HUB");
        yml.addDefault(Messages.GENERAL_CONFIGURATION_LOBBY_ITEMS_LORE.replace("%path%", "leave"), Collections.singletonList("&fClick-gauche pour quitter le bedwars!"));
        /* Pre Game Command Items */
        yml.addDefault(Messages.GENERAL_CONFIGURATION_WAITING_ITEMS_NAME.replace("%path%", "stats"), "&eStats");
        yml.addDefault(Messages.GENERAL_CONFIGURATION_WAITING_ITEMS_LORE.replace("%path%", "stats"), Collections.singletonList("&fClick-gauche pour voir vos stats!"));
        yml.addDefault(Messages.GENERAL_CONFIGURATION_WAITING_ITEMS_NAME.replace("%path%", "leave"), "&eRetour to Lobby");
        yml.addDefault(Messages.GENERAL_CONFIGURATION_WAITING_ITEMS_LORE.replace("%path%", "leave"), Collections.singletonList("&fClick-gauche pour quitter l'arène!"));
        /* Spectator Command Items */
        yml.addDefault(Messages.GENERAL_CONFIGURATION_SPECTATOR_ITEMS_NAME.replace("%path%", "teleporter"), "&eTéléporteur");
        yml.addDefault(Messages.GENERAL_CONFIGURATION_SPECTATOR_ITEMS_NAME.replace("%path%", "leave"), "&eRetour au lobby");
        yml.addDefault(Messages.GENERAL_CONFIGURATION_SPECTATOR_ITEMS_LORE.replace("%path%", "leave"), Collections.singletonList("&fClick-gauche pour quitter l'arène!"));

        /* save default items messages for stats gui */
        yml.addDefault(Messages.PLAYER_STATS_GUI_INV_NAME, "&8%bw_player% Stats");
        addDefaultStatsMsg(yml, "wins", "&6Victoires", "&f%bw_wins%");
        addDefaultStatsMsg(yml, "losses", "&6Défaites", "&f%bw_losses%");
        addDefaultStatsMsg(yml, "kills", "&6Kills", "&f%bw_kills%");
        addDefaultStatsMsg(yml, "deaths", "&6Morts", "&f%bw_deaths%");
        addDefaultStatsMsg(yml, "final-kills", "&6Kills définitifs", "&f%bw_final_kills%");
        addDefaultStatsMsg(yml, "final-deaths", "&6Morts définitives", "&f%bw_final_deaths%");
        addDefaultStatsMsg(yml, "beds-destroyed", "&6Lits détruits", "&f%bw_beds%");
        addDefaultStatsMsg(yml, "first-play", "&6Première partie", "&f%bw_play_first%");
        addDefaultStatsMsg(yml, "last-play", "&6Dernière partie", "&f%bw_play_last%");
        addDefaultStatsMsg(yml, "games-played", "&6Parties jouées", "&f%bw_games_played%");

        yml.addDefault(Messages.SCOREBOARD_DEFAULT_WAITING, Arrays.asList("&f&lBED WARS", "&7%bw_date% &8%bw_server_id%", "", "&fCarte: &a%bw_map%", "", "&fJoueurs: &a%bw_on%/%bw_max%", "", "&fAttente...", "", "&fMode: &a%bw_group%", "&fVersion: &7%bw_version%", "", "&e%bw_server_ip%"));
        yml.addDefault(Messages.SCOREBOARD_DEFAULT_STARTING, Arrays.asList("&f&lBED WARS", "&7%bw_date% &8%bw_server_id%", "", "&fCarte: &a%bw_map%", "", "&fJoueurs: &a%bw_on%/%bw_max%", "", "&fDémarrage dans &a%bw_time%s", "", "&fMode: &a%bw_group%", "&fVersion: &7%bw_version%", "", "&e%bw_server_ip%"));
        yml.addDefault(Messages.SCOREBOARD_DEFAULT_PLAYING, Arrays.asList("&e&lBED WARS", "&7%bw_date%", "", "&f%bw_next_event% in &a%bw_time%", "",
                "%bw_team_1%", "%bw_team_2%", "%bw_team_3%", "%bw_team_4%", "%bw_team_5%", "%bw_team_6%", "%bw_team_7%", "%bw_team_8%", "", "&e%bw_server_ip%"));

        yml.addDefault("scoreboard.Doubles.playing", Arrays.asList("&e&lBED WARS", "&7%bw_date%", "", "&f%bw_next_event% in &a%bw_time%", "",
                "%bw_team_1%", "%bw_team_2%", "%bw_team_3%", "%bw_team_4%", "%bw_team_5%", "%bw_team_6%", "%bw_team_7%", "%bw_team_8%", "", "&e%bw_server_ip%"));

        yml.addDefault("scoreboard.3v3v3v3.playing", Arrays.asList("&e&lBED WARS", "&7%bw_date%", "", "&f%bw_next_event% in &a%bw_time%}", "",
                "%bw_team_1%", "%bw_team_2%", "%bw_team_3%", "%bw_team_4%", "", "&fKills: &a%bw_kills%", "&fKills définitifs: &a%bw_final_kills%", "&fLits détruits: &a%bw_beds%", "", "&e%bw_server_ip%"));

        yml.addDefault("scoreboard.4v4v4v4.playing", Arrays.asList("&e&lBED WARS", "&7%bw_date%", "", "&f%bw_next_event% in &a%bw_time%", "",
                "%bw_team_1%", "%bw_team_2%", "%bw_team_3%", "%bw_team_4%", "", "&fKills: &a%bw_kills%", "&fKills définitifs: &a%bw_final_kills%", "&fLits détruits: &a%bw_beds%", "", "&e%bw_server_ip%"));

        yml.addDefault(Messages.SCOREBOARD_LOBBY, Arrays.asList("&6&lBedWars,&4&lB&6&ledWars,&c&lB&4&le&6&ldWars,&6&lB&c&le&4&ld&6&lWars,&6&lBe&c&ld&4&lW&6&lars,&6&lBed&c&lW&4&la&6&lrs,&6&lBedW&c&la&4&lr&6&ls,&6&lBedWa&c&lr&4&ls,&6&lBedWar&c&ls,&6&lBedWars",
                "&fVotre Niveau: %bw_level%", "", "&fProgression: &a%bw_current_xp%&7/&b%bw_required_xp%", "%bw_progress%", "", "&7%player%", "", "&fPièces: &a%bw_money%", "", "&fVictoires Totales: &a%bw_wins%", "&fKills Totaux: &a%bw_kills%", "", "&e%bw_server_ip%"));

        //
        yml.addDefault(Messages.SHOP_INDEX_NAME, "&8Achat rapide");
        yml.addDefault(Messages.SHOP_QUICK_ADD_NAME, "&8Rajout a l'Achat rapide ...");
        yml.addDefault(Messages.SHOP_INSUFFICIENT_MONEY, "%bw_lang_prefix%&cVous n'avez pas suffisamment de %bw_currency%! Vous avez besoin %bw_amount% de plus!");
        yml.addDefault(Messages.SHOP_NEW_PURCHASE, "%bw_lang_prefix%&aVous avez acheté &6%bw_item%");
        yml.addDefault(Messages.SHOP_ALREADY_BOUGHT, "%bw_lang_prefix%&cVous avez déjà acheté ca!");
        yml.addDefault(Messages.SHOP_ALREADY_HIGHER_TIER, "%bw_lang_prefix%&cVous avez déjà le tier maximum de cette item.");
        yml.addDefault(Messages.SHOP_UTILITY_NPC_SILVERFISH_NAME, "%bw_team_color%&l%bw_team_name% &r%bw_team_color%Moucheron");
        yml.addDefault(Messages.SHOP_UTILITY_NPC_IRON_GOLEM_NAME, "%bw_team_color%%bw_despawn_time%s &8[ %bw_team_color%%bw_health%&8]");
        yml.addDefault(Messages.SHOP_SEPARATOR_NAME, "&8⇧ Catégories");
        yml.addDefault(Messages.SHOP_SEPARATOR_LORE, Collections.singletonList("&8⇩ Items"));
        yml.addDefault(Messages.SHOP_QUICK_BUY_NAME, "&bAchat Rapide");
        yml.addDefault(Messages.SHOP_QUICK_BUY_LORE, new ArrayList<>());
        yml.addDefault(Messages.SHOP_QUICK_EMPTY_NAME, "&cCase vide!");
        yml.addDefault(Messages.SHOP_QUICK_EMPTY_LORE, Arrays.asList("&7Ceci est une case d'Achat rapide!", "&bClick accroupi &7sur n'importe quelle item", "&7dans le shop pour l'ajouter ici."));
        yml.addDefault(Messages.SHOP_CAN_BUY_COLOR, "&a");
        yml.addDefault(Messages.SHOP_CANT_BUY_COLOR, "&c");
        yml.addDefault(Messages.SHOP_LORE_STATUS_CAN_BUY, "&eClick pour acheter!");
        yml.addDefault(Messages.SHOP_LORE_STATUS_CANT_AFFORD, "&cVous n'avez pas suffisamment de %bw_currency%!");
        yml.addDefault(Messages.SHOP_LORE_STATUS_MAXED, "&aMAXIMISÉ!");
        yml.addDefault(Messages.SHOP_LORE_STATUS_ARMOR, "&aÉQUIPÉ!");
        yml.addDefault(Messages.SHOP_LORE_QUICK_ADD, "&bClick accroupi pour ajouter a l'Achat Rapide");
        yml.addDefault(Messages.SHOP_LORE_QUICK_REMOVE, "&bClick accroupi pour retirer de l'Achat Rapide!");


        addCategoryMessages(yml, ConfigPath.SHOP_PATH_CATEGORY_BLOCKS, "&8Blocks", "&aBlocks", Collections.singletonList("&eClick pour voir!"));

        addContentMessages(yml, "wool", ConfigPath.SHOP_PATH_CATEGORY_BLOCKS, "%bw_color%Laine", Arrays.asList("&7Cout: &f%bw_cost% %bw_currency%", "", "&7Bon pour faire des ponts a", "&7travers des iles. se transforme en",
                "&7la couleur de votre équipe.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "clay", ConfigPath.SHOP_PATH_CATEGORY_BLOCKS, "%bw_color%Argile Dure", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "&7Block basic pour defender le lit.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "glass", ConfigPath.SHOP_PATH_CATEGORY_BLOCKS, "%bw_color%Verre anti explosion", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "&7Immunisé aux explosions.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "stone", ConfigPath.SHOP_PATH_CATEGORY_BLOCKS, "%bw_color%Pièrre de l'end", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "&7Block solide pour défendre le lit.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "ladder", ConfigPath.SHOP_PATH_CATEGORY_BLOCKS, "%bw_color%Echelle", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "&7Utile pour aider un chat", "&7bloqué dans un arbre.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "obsidian", ConfigPath.SHOP_PATH_CATEGORY_BLOCKS, "%bw_color%Obsidienne", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "&7Protection extreme pour votre lit.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "wood", ConfigPath.SHOP_PATH_CATEGORY_BLOCKS, "%bw_color%Bois", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "&7Block solide pour défendre le lit", "", "%bw_quick_buy%", "%bw_buy_status%"));

        addCategoryMessages(yml, ConfigPath.SHOP_PATH_CATEGORY_MELEE, "&8Melée", "&aMelée", Collections.singletonList("&eClick pour voir!"));

        addContentMessages(yml, "stone-sword", ConfigPath.SHOP_PATH_CATEGORY_MELEE, "%bw_color%Epée en pièrre", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "iron-sword", ConfigPath.SHOP_PATH_CATEGORY_MELEE, "%bw_color%Epée en Fer", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "diamond-sword", ConfigPath.SHOP_PATH_CATEGORY_MELEE, "%bw_color%Epée en Diamant", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "stick", ConfigPath.SHOP_PATH_CATEGORY_MELEE, "%bw_color%Baton (Recul I)", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "%bw_quick_buy%", "%bw_buy_status%"));

        addCategoryMessages(yml, ConfigPath.SHOP_PATH_CATEGORY_ARMOR, "&8Armures", "&aArmures", Collections.singletonList("&eClick pour voir!"));

        addContentMessages(yml, "chainmail", ConfigPath.SHOP_PATH_CATEGORY_ARMOR, "%bw_color%Armure Permanente en Cote de maille", Arrays.asList("&7Cout: %bw_cost% %bw_currency%",
                "", "&7Chainmail leggings and boots", "&7which you will always spawn", "&7with.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "iron-armor", ConfigPath.SHOP_PATH_CATEGORY_ARMOR, "%bw_color%Armure Permanente en Fer", Arrays.asList("&7Cout: %bw_cost% %bw_currency%",
                "", "&7Iron leggings and boots which", "&7you will always spawn with.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "diamond-armor", ConfigPath.SHOP_PATH_CATEGORY_ARMOR, "%bw_color%Armure Permanente en Diamant", Arrays.asList("&7Cout: %bw_cost% %bw_currency%",
                "", "&7Diamond leggings and boots which", "&7you will always crush with.", "", "%bw_quick_buy%", "%bw_buy_status%"));

        addCategoryMessages(yml, ConfigPath.SHOP_PATH_CATEGORY_TOOLS, "&8Tools", "&aTools", Collections.singletonList("&eClick pour voir!"));

        addContentMessages(yml, "shears", ConfigPath.SHOP_PATH_CATEGORY_TOOLS, "%bw_color%Cisailles Permanentes", Arrays.asList("&7Cout: %bw_cost% %bw_currency%",
                "", "&7Bon pour se débarasser de la laine. Vous", "&7allez toujours réapparaitre avec.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "pickaxe", ConfigPath.SHOP_PATH_CATEGORY_TOOLS, "%bw_color%Pioche %bw_tier%", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "&7Niveau: &e%bw_tier%",
                "", "&7Ceci est un item que l'on peut mettre a niveau.", "&7Il va perdre un niveau.", "&7a chaque mort!", "", "&7Vous", "&7allez toujours réapparaitre avec", "&7un niveau de moin.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "axe", ConfigPath.SHOP_PATH_CATEGORY_TOOLS, "%bw_color%Hache %bw_tier%", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "&7Niveau: &e%bw_tier%",
                "", "&7Ceci est un item que l'on peut mettre a niveau.", "&7Il va perdre un niveau.", "&7a chaque mort!", "", "&7Vous", "&7allez toujours réapparaitre avec", "&7un niveau de moin.", "", "%bw_quick_buy%", "%bw_buy_status%"));

        addCategoryMessages(yml, ConfigPath.SHOP_PATH_CATEGORY_RANGED, "&8Arcs", "&aArcs", Collections.singletonList("&eClick pour voir!"));

        addContentMessages(yml, "arrow", ConfigPath.SHOP_PATH_CATEGORY_RANGED, "%bw_color%Arrow", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "bow1", ConfigPath.SHOP_PATH_CATEGORY_RANGED, "%bw_color%Bow", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "bow2", ConfigPath.SHOP_PATH_CATEGORY_RANGED, "%bw_color%Bow (Power I)", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "bow3", ConfigPath.SHOP_PATH_CATEGORY_RANGED, "%bw_color%Bow (Power I, Punch I)", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "%bw_quick_buy%", "%bw_buy_status%"));

        addCategoryMessages(yml, ConfigPath.SHOP_PATH_CATEGORY_POTIONS, "&8Potions", "&aPotions", Collections.singletonList("&eClick pour voir!"));

        addContentMessages(yml, "speed-potion", ConfigPath.SHOP_PATH_CATEGORY_POTIONS, "%bw_color%Potion de Vitesse II (45 secondes)", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "jump-potion", ConfigPath.SHOP_PATH_CATEGORY_POTIONS, "%bw_color%Potion de Jump V (45 secondes)", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "invisibility", ConfigPath.SHOP_PATH_CATEGORY_POTIONS, "%bw_color%Potion d'invisibilité (30 secondes)", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "%bw_quick_buy%", "%bw_buy_status%"));

        addCategoryMessages(yml, ConfigPath.SHOP_PATH_CATEGORY_UTILITY, "&8Utilitaires", "&aUtilitaires", Collections.singletonList("&eClick pour voir!"));

        addContentMessages(yml, "golden-apple", ConfigPath.SHOP_PATH_CATEGORY_UTILITY, "%bw_color%Pomme d'or", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "&7Bonne source de régeneration.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "bedbug", ConfigPath.SHOP_PATH_CATEGORY_UTILITY, "%bw_color%Moucheron", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "&7Fait apparaitre des moucherons ou",
                "&7ou la boulle de neige atterrit, utile", "&7pour distraire vos ennemis. Dure 15 secondes.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "dream-defender", ConfigPath.SHOP_PATH_CATEGORY_UTILITY, "%bw_color%Golem de fer", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "&7Le golem de fer est utile pour",
                "&fdéfendre votre base. Dure 4 minutes.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "fireball", ConfigPath.SHOP_PATH_CATEGORY_UTILITY, "%bw_color%Boulle de feu", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "&7Click-gauche pour lancer! Bon pour",
                "&7pousser les ennemis sur", "&7des ponts", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "tnt", ConfigPath.SHOP_PATH_CATEGORY_UTILITY, "%bw_color%TNT", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "&7S'allume instantanément, approprié",
                "&7pour faire exploser des choses!", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "ender-pearl", ConfigPath.SHOP_PATH_CATEGORY_UTILITY, "%bw_color%Perle de l'ender", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "&7Le moyen le plus facile d'envahir",
                "&7les basses ennemies.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "water-bucket", ConfigPath.SHOP_PATH_CATEGORY_UTILITY, "%bw_color%Seau d'eau", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "&7Bon moyen pour ralentir",
                "&7les ennemsi en approche. Protège aussi", "&7contre la TNT.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "bridge-egg", ConfigPath.SHOP_PATH_CATEGORY_UTILITY, "%bw_color%Oeuf de pont", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "&7Cette oeuf crée un pont",
                "&7dans la direction dans laquelle il est jeté.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "magic-milk", ConfigPath.SHOP_PATH_CATEGORY_UTILITY, "%bw_color%Lait Magique", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "&7Empêche de déclencher les pièges ennemis pour 60",
                "&7secondes après la consommation.", "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "sponge", ConfigPath.SHOP_PATH_CATEGORY_UTILITY, "%bw_color%Éponge", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "&7Bien pour absorber de l'eau.",
                "", "%bw_quick_buy%", "%bw_buy_status%"));
        addContentMessages(yml, "tower", ConfigPath.SHOP_PATH_CATEGORY_UTILITY, "%bw_color%Tour dépliable compacte", Arrays.asList("&7Cout: %bw_cost% %bw_currency%", "", "&7La tour dépliable compacte est très utile", "&7pour défendre!", "", "%bw_quick_buy%", "%bw_buy_status%"));

        //
        yml.addDefault(Messages.MEANING_NO_TRAP, "Pas de piège!");
        yml.addDefault(Messages.FORMAT_UPGRADE_TRAP_COST, "&7Cout: %bw_currency_color%%bw_cost% %bw_currency%");
        yml.addDefault(Messages.FORMAT_UPGRADE_COLOR_CAN_AFFORD, "&e");
        yml.addDefault(Messages.FORMAT_UPGRADE_COLOR_CANT_AFFORD, "&c");
        yml.addDefault(Messages.FORMAT_UPGRADE_COLOR_UNLOCKED, "&a");
        yml.addDefault(Messages.FORMAT_UPGRADE_TIER_LOCKED, "&7");
        yml.addDefault(Messages.FORMAT_UPGRADE_TIER_UNLOCKED, "&a");
        yml.addDefault(Messages.UPGRADES_LORE_REPLACEMENT_CLICK_TO_BUY, "%bw_color%Click pour acheter!");
        yml.addDefault(Messages.UPGRADES_LORE_REPLACEMENT_INSUFFICIENT_MONEY, "%bw_color%Vous n'avez pas suffisamment de %bw_currency%");
        yml.addDefault(Messages.UPGRADES_LORE_REPLACEMENT_INSUFFICIENT_SPACE, "&eVous n'avez pas suffisamment de place d'inventaire pour acheter cette item!");
        yml.addDefault(Messages.UPGRADES_LORE_REPLACEMENT_LOCKED, "&cVERROUILLÉ");
        yml.addDefault(Messages.UPGRADES_LORE_REPLACEMENT_UNLOCKED, "%bw_color%DÉVERROUILLER");
        yml.addDefault(Messages.UPGRADES_UPGRADE_BOUGHT_CHAT, "&a%bw_player% a acheté &6%bw_upgrade_name%");
        yml.addDefault(Messages.UPGRADES_UPGRADE_ALREADY_CHAT, "&cVous avez déjà déverrouillé cette amélioration!");
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("%bw_name%", "forge").replace("%bw_tier%", "tier-1"), "%bw_color%Forge de Fer");
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_LORE.replace("%bw_name%", "forge"),
                Arrays.asList("&7Améliorez les resources qui apparaissent", "&7sur votre ile.", "",
                        "{tier_1_color}Niveau 1: +50% de Resources, &b{tier_1_cost} {tier_1_currency}",
                        "{tier_2_color}Niveau 2: +100% de Resources, &b{tier_2_cost} {tier_2_currency}",
                        "{tier_3_color}Niveau 3: Les Émeraudes apparaissent, &b{tier_3_cost} {tier_3_currency}",
                        "{tier_4_color}Niveau 4: +200% sur toute les Resources, &b{tier_4_cost} {tier_4_currency}", ""));
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("%bw_name%", "forge").replace("%bw_tier%", "tier-2"), "%bw_color%Forge d'or");
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("%bw_name%", "forge").replace("%bw_tier%", "tier-3"), "%bw_color%Forge d'émeraudes");
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("%bw_name%", "forge").replace("%bw_tier%", "tier-4"), "%bw_color%Fonderie");
        yml.addDefault(Messages.UPGRADES_CATEGORY_ITEM_NAME_PATH + "traps", "&eAcheter un piège");
        yml.addDefault(Messages.UPGRADES_CATEGORY_ITEM_LORE_PATH + "traps", Arrays.asList("&7Les pièges achetées vont etre", "&7en fille sur la droite.", "", "&eClick pour naviguer!"));
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("%bw_name%", "swords").replace("%bw_tier%", "tier-1"), "%bw_color%Épées tranchantes");
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_LORE.replace("%bw_name%", "swords"),
                Arrays.asList("&7Votre équipe gagne définitivement", "&7Tranchant I sur toutes les épées et", "&7Haches!", "", "{tier_1_color}Cout: &b{tier_1_cost} {tier_1_currency}", ""));
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("%bw_name%", "armor").replace("%bw_tier%", "tier-1"), "%bw_color%Armure Renforcée I");
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_LORE.replace("%bw_name%", "armor"),
                Arrays.asList("&7Votre équipe gagne définitivement", "&7Protection sur toutes les pièces d'armures!", "",
                        "{tier_1_color}Niveau 1: Protection I, &b{tier_1_cost} {tier_1_currency}",
                        "{tier_2_color}Niveau 2: Protection II, &b{tier_2_cost} {tier_2_currency}",
                        "{tier_3_color}Niveau 3: Protection III, &b{tier_3_cost} {tier_3_currency}",
                        "{tier_4_color}Niveau 4: Protection IV, &b{tier_4_cost} {tier_4_currency}", ""));
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("%bw_name%", "armor").replace("%bw_tier%", "tier-2"), "%bw_color%Armure Renforcée II");
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("%bw_name%", "armor").replace("%bw_tier%", "tier-3"), "%bw_color%Armure Renforcée III");
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("%bw_name%", "armor").replace("%bw_tier%", "tier-4"), "%bw_color%Armure Renforcée IV");
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("%bw_name%", "miner").replace("%bw_tier%", "tier-1"), "%bw_color%Mineur Fou I");
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_LORE.replace("%bw_name%", "miner"),
                Arrays.asList("&7Votre équipe gagne définitivement", "&7une vitesse de minage accrue.", "", "{tier_1_color}Niveau 1: Célérité I, &b{tier_1_cost} {tier_1_currency}",
                        "{tier_2_color}Niveau 2: Célérité II, &b{tier_2_cost} {tier_2_currency}", ""));
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("%bw_name%", "miner").replace("%bw_tier%", "tier-2"), "%bw_color%Mineur Fou II");
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("%bw_name%", "heal-pool").replace("%bw_tier%", "tier-1"), "%bw_color%Point de régeneration");
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_LORE.replace("%bw_name%", "heal-pool"),
                Arrays.asList("&7Crée une zone de régeneration", "&7autour de votre base!", "", "{tier_1_color}Cout: &b{tier_1_cost} {tier_1_currency}", ""));
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("%bw_name%", "dragon").replace("%bw_tier%", "tier-1"), "%bw_color%Dragon Supplémentaire");
        yml.addDefault(Messages.UPGRADES_UPGRADE_TIER_ITEM_LORE.replace("%bw_name%", "dragon"),
                Arrays.asList("&7Votre team aura 2 dragons", "&7au lieu de 1 durant le match à mort!", "", "{tier_1_color}Cout: &b{tier_1_cost} {tier_1_currency}", ""));
        yml.addDefault(Messages.UPGRADES_SEPARATOR_ITEM_NAME_PATH + "glass", "&8⬆&7Achetable");
        yml.addDefault(Messages.UPGRADES_SEPARATOR_ITEM_LORE_PATH + "glass", Collections.singletonList("&8⬇&7Fille de pièges"));
        yml.addDefault(Messages.UPGRADES_TRAP_SLOT_ITEM_NAME_PATH + "first", "%bw_color%Piège #1: %bw_name%");
        yml.addDefault(Messages.UPGRADES_TRAP_SLOT_ITEM_LORE1_PATH + "first", Arrays.asList("&7Le première ennemi a", "&7renter dans votre base va déclencher", "&7ce piège!"));
        yml.addDefault(Messages.UPGRADES_TRAP_SLOT_ITEM_LORE2_PATH + "first",
                Arrays.asList("", "&7Acheter ce piège le mettras", "&7 dans la fille ici. Son cout", "&7seras proportionnel au", "&7nombres de traps dans la fille.", "", "&7Prochain Piège: &b%bw_cost% %bw_currency%"));
        yml.addDefault(Messages.UPGRADES_TRAP_SLOT_ITEM_NAME_PATH + "second", "%bw_color%Piège #2: %bw_name%");
        yml.addDefault(Messages.UPGRADES_TRAP_SLOT_ITEM_LORE1_PATH + "second", Arrays.asList("&7Le deuxième ennemi a", "&7rentrer dans votre base va déclencher", "&7ce piège!"));
        yml.addDefault(Messages.UPGRADES_TRAP_SLOT_ITEM_LORE2_PATH + "second",
                Arrays.asList("", "&7Acheter ce piège le mettras", "&7 dans la fille ici. Son cout", "&7seras proportionnel au", "&7nombres de traps dans la fille.", "", "&7Prochain Piège: &b%bw_cost% %bw_currency%"));
        yml.addDefault(Messages.UPGRADES_TRAP_SLOT_ITEM_NAME_PATH + "third", "%bw_color%Piège #3: %bw_name%");
        yml.addDefault(Messages.UPGRADES_TRAP_SLOT_ITEM_LORE1_PATH + "third", Arrays.asList("&7The third enemy to walk", "&7into your base will trigger", "&7ce piège!"));
        yml.addDefault(Messages.UPGRADES_TRAP_SLOT_ITEM_LORE2_PATH + "third",
                Arrays.asList("", "&7Acheter ce piège le mettras", "&7 dans la fille ici. Son cout", "&7seras proportionnel au", "&7nombres de traps dans la fille.", "", "&7Prochain Piège: &b%bw_cost% %bw_currency%"));
        yml.addDefault(Messages.UPGRADES_BASE_TRAP_ITEM_NAME_PATH + "1", "%bw_color%C'est un piège!");
        yml.addDefault(Messages.UPGRADES_BASE_TRAP_ITEM_LORE_PATH + "1", Arrays.asList("&7Inflige cécité et lenteur", "&7pour 5 secondes.", ""));
        yml.addDefault(Messages.UPGRADES_BASE_TRAP_ITEM_NAME_PATH + "2", "%bw_color%Piège anti offensif");
        yml.addDefault(Messages.UPGRADES_BASE_TRAP_ITEM_LORE_PATH + "2", Arrays.asList("&7Donne vitesse 1 pour 15 secondes aux", "&7joueurs alliées pres de la base.", ""));
        yml.addDefault(Messages.UPGRADES_BASE_TRAP_ITEM_NAME_PATH + "3", "%bw_color%Piège d'alarme");
        yml.addDefault(Messages.UPGRADES_BASE_TRAP_ITEM_LORE_PATH + "3", Arrays.asList("&7Révèle les joueurs invisibles ansi", "&7que leurs nom et équipe.", ""));
        yml.addDefault(Messages.UPGRADES_BASE_TRAP_ITEM_NAME_PATH + "4", "%bw_color%Piège Lassitude des mineurs");
        yml.addDefault(Messages.UPGRADES_BASE_TRAP_ITEM_LORE_PATH + "4", Arrays.asList("&7Inflige l'effet de lassitude des mineurs pour", "&710secondes.", ""));
        yml.addDefault(Messages.UPGRADES_SEPARATOR_ITEM_NAME_PATH + "back", "&aRetour");
        yml.addDefault(Messages.UPGRADES_SEPARATOR_ITEM_LORE_PATH + "back", Collections.singletonList("&7Aux Améliorations & Pièges"));
        yml.addDefault(Messages.UPGRADES_CATEGORY_GUI_NAME_PATH + "traps", "&8Mettre un piège en fille");
        yml.addDefault(Messages.UPGRADES_TRAP_QUEUE_LIMIT, "&cFille de pièges pleine!");
        yml.addDefault(Messages.UPGRADES_TRAP_DEFAULT_MSG, "&c&l%bw_trap% a été déclenchée!");
        yml.addDefault(Messages.UPGRADES_TRAP_DEFAULT_TITLE, "&cPIÈGE DÉCLENCHÉ!");
        yml.addDefault(Messages.UPGRADES_TRAP_DEFAULT_SUBTITLE, "&fLe piège %bw_trap% a été déclenché!");
        yml.addDefault(Messages.UPGRADES_TRAP_CUSTOM_MSG + "3", "&c&lAlarme déclenchée par &7&l%bw_player% &c&lde l'équipe %bw_color%&l%bw_team%!");
        yml.addDefault(Messages.UPGRADES_TRAP_CUSTOM_TITLE + "3", "&c&lALARME!!!");
        yml.addDefault(Messages.UPGRADES_TRAP_CUSTOM_SUBTITLE + "3", "&fAlarme déclenchée par l'équipe %bw_color%%bw_team%!");
        generateNPCMessages(yml, "default");
        save();
        setPrefix(m(Messages.PREFIX));
        setPrefixStatic(m(Messages.PREFIX));
    }

    @Override
    public void generateNPCMessages(YamlConfiguration yml, String group){
        yml.addDefault(Messages.NPC_NAME_TEAM_UPGRADES.replace("%group%", group), Arrays.asList("&bAMÉLIORATIONS D'ÉQUIPES", "&e&lCLICK DROIT"));
        yml.addDefault(Messages.NPC_NAME_SOLO_UPGRADES.replace("%group%", group), Arrays.asList("&bAMÉLIORATIONS SOLO", "&e&lCLICK DROIT"));
        yml.addDefault(Messages.NPC_NAME_TEAM_SHOP.replace("%group%", group), Arrays.asList("&bBOUTIQUE D'ÉQUIPE", "&e&lCLICK DROIT"));
        yml.addDefault(Messages.NPC_NAME_SOLO_SHOP.replace("%group%", group), Arrays.asList("&bBOUTIQUE D'ITEM", "&e&lCLICK DROIT"));
    }
}