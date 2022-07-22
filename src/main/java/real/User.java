package real;

import io.Session;
import lombok.SneakyThrows;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import patch.Friend;
import patch.battle.ClanBattle;
import patch.clan.ClanTerritoryData;
import patch.Constants;
import patch.ItemShinwaManager;
import patch.interfaces.SendMessage;
import patch.tournament.Tournament;
import server.GameScr;
import server.SQLManager;
import server.Service;
import server.util;
import tasks.TaskHandle;
import threading.Manager;
import threading.Map;
import threading.Message;
import threading.Server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static real.User.TypeTBLOption.ALL_MAP;
import static server.util.*;
import java.sql.ResultSet;

public class User extends Actor implements SendMessage {
    public static final String TẤT_CẢ_CÁC_KHU_ĐẶT_CƯỢC_ĐỀU_FULL = "Tất cả các khu đặt cược đều full";
    public static long MIN_TIME_RESET_POINT;
    public String username;
    public String version;
    public Session session;
    public Ninja nj;
    public String giftcode = "";
    public String passold;
    public String passnew;
    public String nameUS;
    public String idItemGF;
    public String itemQuantityGF;
    public String UserGF;
    public String itemUpgradeGF;
    public String itemSysGF;
    public String luongGF;
    public String xuGF;
    public String yenGF;
    public String messGF;
    public String messTB;
    public long expiredTime;

    private ClanTerritoryData clanTerritoryData;

    private long lastTimeUseItem = System.currentTimeMillis();
    Server server;

    public boolean nhanQua = false;
    private int clanTerritoryId;

    public enum TypeTBLOption {
        NOT_USE(-1),
        $240(240),
        $480(480),
        ALL_MAP(888888),
        USEFUL(2),
        PICK_ALL(3);
        private int value;

        TypeTBLOption(int i) {
            this.value = i;
        }

        public int getValue() {
            return value;
        }
    }

    public TypeTBLOption typeTBLOption = ALL_MAP;
    public boolean activeTBL = false;
    public boolean filter = false;

    public User() {
        this.username = null;
        this.version = null;
        this.session = null;
        this.setNj(null);
        this.passold = "";
        this.passnew = "";
        this.setClanTerritoryId(-1);
        this.server = Server.getInstance();

    }

    public boolean containsItem(int id) {
        for (Item item : this.nj.ItemBag) {
            if (item != null && item.id == id) {
                return true;
            }
        }
        return false;
    }

    public void giftcode() {
        SQLManager.executeQuery("SELECT `*` FROM `giftcode` WHERE (`giftcode` LIKE'" + giftcode + "');", (checkGift) -> {

            if (checkGift == null || !checkGift.first()) {
                session.sendMessageLog("Mã quà tặng không hợp lệ");
                return;
            } else {
                String userEntered = "";
                String messTB = "";
                int xu = 0;
                int yen = 0;
                int luong = 0;
                short itemId = 0;
                short itemId1 = 0;
                short itemId2 = 0;
                short itemId3 = 0;
                short itemId4 = 0;
                short itemId5 = 0;
                int itemQuantity = 0;
                int itemQuantity1 = 0;
                int itemQuantity2 = 0;
                int itemQuantity3 = 0;
                int itemQuantity4 = 0;
                int itemQuantity5 = 0;
                int luotnhap = 0;
                int gioihan = 0;
                int idgift = 0;
                idgift = checkGift.getInt("idgift");
                userEntered = checkGift.getString("username");
                luotnhap = checkGift.getInt("luotnhap");
                gioihan = checkGift.getInt("gioihan");
                xu = checkGift.getInt("xu");
                yen = checkGift.getInt("yen");
                luong = checkGift.getInt("luong");
                messTB = checkGift.getString("messTB");
                itemId = checkGift.getShort("itemId");
                itemQuantity = checkGift.getInt("itemQuantity");
                itemId1 = checkGift.getShort("itemId1");
                itemQuantity1 = checkGift.getInt("itemQuantity1");
                itemId2 = checkGift.getShort("itemId2");
                itemQuantity2 = checkGift.getInt("itemQuantity2");
                itemId3 = checkGift.getShort("itemId3");
                itemQuantity3 = checkGift.getInt("itemQuantity3");
                itemId4 = checkGift.getShort("itemId4");
                itemQuantity4 = checkGift.getInt("itemQuantity4");
                itemId5 = checkGift.getShort("itemId5");
                itemQuantity5 = checkGift.getInt("itemQuantity5");

                if (userEntered.contains(username)) {
                    session.sendMessageLog("Mỗi tài khoản chỉ được nhập mã quà tặng này 1 lần");
                    return;
                }

                if (luotnhap >= gioihan) {
                    session.sendMessageLog("Mã quà tặng đã đạt giới hạn lượt nhập");
                    return;
                }
                luotnhap += 1;
                userEntered += username + ", ";

                if (xu != 0) {
                    nj.upxuMessage(xu);
                }
                if (yen != 0) {
                    nj.upyenMessage(yen);
                }
                if (luong != 0) {
                    upluongMessage(luong);
                }
                if (itemId != 0) {
                    Item it = new Item();
                    for (byte i = 0; i < itemQuantity; i++) {
                        it = ItemData.itemDefault(itemId);
                        nj.addItemBag(true, it);
                    }
                }
                if (itemId1 != 0) {
                    Item it1 = new Item();
                    for (byte i = 0; i < itemQuantity1; i++) {
                        it1 = ItemData.itemDefault(itemId1);
                        nj.addItemBag(true, it1);
                    }
                }
                if (itemId2 != 0) {
                    Item it2 = new Item();
                    for (byte i = 0; i < itemQuantity2; i++) {
                        it2 = ItemData.itemDefault(itemId2);
                        nj.addItemBag(true, it2);
                    }
                }
                if (itemId3 != 0) {
                    Item it3 = new Item();
                    for (byte i = 0; i < itemQuantity3; i++) {
                        it3 = ItemData.itemDefault(itemId3);
                        nj.addItemBag(true, it3);
                    }
                }
                if (itemId4 != 0) {
                    Item it4 = new Item();
                    for (byte i = 0; i < itemQuantity4; i++) {
                        it4 = ItemData.itemDefault(itemId4);
                        nj.addItemBag(true, it4);
                    }
                }
                if (itemId5 != 0) {
                    Item it5 = new Item();
                    for (byte i = 0; i < itemQuantity5; i++) {
                        it5 = ItemData.itemDefault(itemId5);
                        nj.addItemBag(true, it5);
                    }
                }
                if (messTB.length() > 0) {
                    this.sendYellowMessage(messTB);
                }
                SQLManager.executeUpdate("UPDATE `giftcode` SET `luotnhap`='" + luotnhap + "' WHERE `idgift`=" + idgift + " LIMIT 1;");
                SQLManager.executeUpdate("UPDATE `giftcode` SET `username`='" + userEntered + "' WHERE `idgift`=" + idgift + " LIMIT 1;");
            }
        });

    }


    public void cleanup() {
        this.session = null;
        this.setClanTerritoryData(null);
    }

    public synchronized int upluong(long x) {
        final long luongnew = this.luong + x;
        if (luongnew > 2000000000L) {
            x = 2000000000 - this.luong;
        } else if (luongnew < -2000000000L) {
            x = -2000000000 - this.luong;
        }
        this.luong += (int) x;
        return (int) x;
    }
  
    public static String[] lockusername = new String[] {"%"};

    public static User login(final Session conn, final String user, final String pass) {
        if (user.startsWith("%")) {
            conn.sendMessageLog("Tên tài khoản không hợp lệ");
            return null;
        }
        final User[] u = new User[]{null};
        val query = "SELECT * FROM `player` WHERE (`username`LIKE'" + user + "' AND `password`LIKE'" + pass + "');";
        SQLManager.executeQuery(query, (red) -> {
            if (red != null && red.first()) {
                final int iddb = red.getInt("id");
                final String username = red.getString("username");
                final int luong = red.getInt("luong");
                final byte lock = red.getByte("lock");
                for (int i = 0; i < User.lockusername.length; i++) {
                    if (username.contains(User.lockusername[i])) {
                        conn.sendMessageLog("Tên tài khoản không hợp lệ");
                        return;
                    }
                }
                if (lock == 1) {
                    conn.sendMessageLog("Tài khoản của bạn đã bị khóa hoặc chưa kích hoạt (Phí kích hoạt 10k). Để biết thêm thông tin hãy liên hệ ADMIN");
                    u[0] = null;
                    return;
                }
                final JSONArray jarr = (JSONArray) JSONValue.parse(red.getString("ninja"));
                User p = PlayerManager.getInstance().getPlayer(user);
                if (p != null) {
                    p.session.sendMessageLog("Có người đăng nhập vào tài khoản của bạn");
                    PlayerManager.getInstance().kickSession(p.session);
                    conn.sendMessageLog("Bạn đang đăng nhập tại máy khác. Hãy thử đăng nhập lại");
                    if (p.session != null) {
                        p.session.disconnect();
                    }
                    u[0] = null;
                    return;
                }
                p = new User();

                p.session = conn;
                p.id = iddb;
                p.username = username;
                p.luong = luong;

                try {
                    p.setClanTerritoryId(red.getInt("clanTerritoryId"));
                } catch (Exception e) {
                    p.setClanTerritoryId(-1);
                    e.printStackTrace();
                }

                for (byte i = 0; i < jarr.size(); ++i) {
                    p.sortNinja[i] = jarr.get(i).toString();
                }
                PlayerManager.getInstance().put(p);
                u[0] = p;
            } else {
                conn.sendMessageLog("Thông tin tài khoản hoặc mật khẩu không chính xác");
            }

        });
        return u[0];
    }


    public void messageSubCommand(final Message m) throws IOException {
        final byte b = m.reader().readByte();
        Debug("Cmd -30->" + b);
        switch (b) {
            case -109: {
                this.pluspPoint(m);
                break;
            }
            case -108: {
                this.plusSkillpoints(m);
                break;
            }
            case -107: {
                this.nj.sortBag();
                break;
            }
            case -106: {
                this.nj.sortBox();
                break;
            }
            case -105: {
                this.xuBagtoBox(m);
                break;
            }
            case -104: {
                this.xuBoxtoBag(m);
                break;
            }
            case -103: {
                GameScr.ItemInfo(this, m);
                break;
            }
            case -93: {
                this.nj.getPlace().changerTypePK(this, m);
                break;
            }
            case -88: {
                if (this.nj != null) {
                    this.createParty();
                    break;
                }
                break;
            }
            case -87: {
                if (this.nj != null) {
                    this.changeTeamLeaderParty(m);
                    break;
                }
                break;
            }
            case -86: {
                if (this.nj != null) {
                    this.moveMemberParty(m);
                    break;
                }
                break;
            }
            case -85: {
                this.viewFriend();
                break;
            }
            case -83: {
                this.deleteFriend(m);
                break;
            }
            case -79: {
                useSkill.buffLive(this, m);
                break;
            }
            case -77: {
                if (this.nj != null && this.nj.getPlace() != null) {
                    this.nj.getPlace().openFindParty(this);
                    break;
                }
                break;
            }
            case -67: {
                this.pasteSkill(m);
                break;
            }
            case -65: {
                GameScr.sendSkill(this, m.reader().readUTF());
                break;
            }
            case -63: {
                this.clanInvite(m);
                break;
            }
            case -62: {
                this.acceptInviteClan(m);
                break;
            }
            case -61: {
                this.clanPlease(m);
                break;
            }
            case -60: {
                this.acceptPleaseClan(m);
                break;
            }
            default:
                util.Debug("NOT MATCH sub command b");
        }
    }

    @SneakyThrows
    public void sendInfo() {
        sendInfo(true);
    }

    public void sendInfo(boolean enter) throws IOException {
        if (nj.isHuman)
            restPoint();
        this.nj.hp = this.nj.getMaxHP();
        this.nj.mp = this.nj.getMaxMP();
        final Message m = new Message(-30);
        m.writer().writeByte(-127);
        m.writer().writeInt(this.nj.id);
        m.writer().writeUTF(this.nj.clan.clanName);
        if (!this.nj.clan.clanName.isEmpty()) {
            m.writer().writeByte(this.nj.clan.typeclan);
        }
        m.writer().writeByte(this.nj.getTaskId());
        m.writer().writeByte(this.nj.gender);
        m.writer().writeShort(this.nj.head);
        m.writer().writeByte(this.nj.speed());
        m.writer().writeUTF(this.nj.name);
        m.writer().writeByte(this.nj.pk);
        m.writer().writeByte(this.nj.getTypepk());
        m.writer().writeInt(this.nj.getMaxHP());
        m.writer().writeInt(this.nj.hp);
        m.writer().writeInt(this.nj.getMaxMP());
        m.writer().writeInt(this.nj.mp);
        m.writer().writeLong(this.nj.getExp());
        m.writer().writeLong(this.nj.expdown);

        m.writer().writeShort(this.nj.eff5buffHP());
        m.writer().writeShort(this.nj.eff5buffMP());
        m.writer().writeByte(this.nj.nclass);
        m.writer().writeShort(this.nj.get().getPpoint());
        m.writer().writeShort(this.nj.get().getPotential0());
        m.writer().writeShort(this.nj.get().getPotential1());
        m.writer().writeInt(this.nj.get().getPotential2());
        m.writer().writeInt(this.nj.get().getPotential3());
        m.writer().writeShort(this.nj.get().getSpoint());
        m.writer().writeByte(this.nj.getSkills().size());
        for (short i = 0; i < this.nj.getSkills().size(); ++i) {
            final Skill skill = this.nj.getSkills().get(i);
            m.writer().writeShort(SkillData.Templates(skill.id, skill.point).skillId);
        }
        m.writer().writeInt(this.nj.xu);
        m.writer().writeInt(this.nj.yen);
        m.writer().writeInt(this.luong);
        m.writer().writeByte(this.nj.maxluggage);
        for (int j = 0; j < this.nj.maxluggage; ++j) {
            final Item item = this.nj.ItemBag[j];
            if (item != null) {
                m.writer().writeShort(item.id);
                m.writer().writeBoolean(item.isLock());
                if (ItemData.isTypeBody(item.id) || ItemData.isTypeMounts(item.id) || ItemData.isTypeNgocKham(item.id)) {
                    m.writer().writeByte(item.getUpgrade());
                }
                m.writer().writeBoolean(item.isExpires);
                m.writer().writeShort(item.quantity);
            } else {
                m.writer().writeShort(-1);
            }
        }
        for (int k = 0; k < 16; ++k) {
            final Item item = this.nj.ItemBody[k];
            if (item != null) {
                m.writer().writeShort(item.id);
                m.writer().writeByte(item.getUpgrade());
                m.writer().writeByte(item.sys);
            } else {
                m.writer().writeShort(-1);
            }
        }
        m.writer().writeBoolean(this.nj.isHuman);
        m.writer().writeBoolean(this.nj.isNhanban);
        m.writer().writeShort(-1);
        m.writer().writeShort(-1);
        m.writer().writeShort(-1);
        m.writer().writeShort(-1);

        m.writer().flush();
        this.sendMessage(m);


        m.cleanup();
        this.getMobMe();
        try {
            this.nj.clone = CloneChar.getClone(this.nj);
        } catch (Exception e) {

        }

        if (nj.clone != null) {
            val clone = nj.clone;
            int totalClone = clone.getPotential0() +
                    clone.getPotential1() +
                    clone.getPotential2() +
                    clone.getPotential3() +
                    clone.getPpoint();
            if (totalClone > Level.totalpPoint(clone.getLevel()) + clone.getTiemNangSo() * 10 + clone.getBanghoa() * 10 + 25) {
                this.restPpoint(clone);
            }
        }

        if (enter) {
            Map[] maps = this.server.getMaps();
            for (int i = 0; i < maps.length; i++) {
                Map map = Server.getMapById(i);
                if (map.id == this.nj.getMapid()) {
                    boolean isturn = false;
                    if (map.getXHD() != -1 || map.VDMQ()) {
                        isturn = true;
                        map = Manager.getMapid(this.nj.mapLTD);
                    }
                    for (int l = 0; l < map.area.length; ++l) {
                        if (map.area[l].getNumplayers() < map.template.maxplayers) {
                            if (this.nj.getPlace() != null) {
                                this.nj.getPlace().leave(this);
                            }
                            if (!isturn) {
                                map.area[l].Enter(this);
                            } else {
                                map.area[l].EnterMap0(this.nj);
                            }
                            for (byte n = 0; n < this.nj.getVeff().size(); ++n) {
                                this.addEffectMessage(this.nj.getVeff().get(n));
                            }
                            return;
                        }
                    }
                    map.area[nextInt(map.area.length)].Enter(this);
                    for (byte n2 = 0; n2 < this.nj.getVeff().size(); ++n2) {
                        this.addEffectMessage(this.nj.getVeff().get(n2));
                    }
                }
            }
        }
        val mes = new Message(-155);
        val ds = mes.writer();

        ds.writeInt(0);
        ds.writeByte(nj.get().getPhongLoi());
        ds.writeByte(nj.get().getBanghoa());
        ds.flush();
        sendMessage(mes);
        m.cleanup();
    }


    @SneakyThrows
    void restPoint() {

        int totalPoint = this.nj.getPotential0() + this.nj.getPotential1() + this.nj.getPotential2() + this.nj.getPotential3() + this.nj.getPpoint();

        if (totalPoint > Level.totalpPoint(nj.get().getLevel()) + nj.get().getTiemNangSo() * 10 + nj.get().getBanghoa() * 10 + 25) {
            this.restPpoint(nj);
            this.restSpoint();
            this.nj.setExp(Level.getMaxExp(this.nj.getLevel()));
        }


    }

    public void messageNotMap(final Message m) throws Exception {
        final byte cmd = m.reader().readByte();
        Debug("-28->" + cmd);
        ClanManager clan = null;
        switch (cmd) {
            case -126: {
                this.selectNhanVat(m);
                break;
            }
            case -125: {
                this.createNinja(m);
                break;
            }
            case -122: {
                GameScr.SendFile(session, -28, "res/msg/-28_-122");
//                this.server.manager.sendData(this);
                break;
            }
            case -121: {
                this.server.manager.sendMap(this);
                break;
            }
            case -120: {
                this.server.manager.sendSkill(this);
                break;
            }
            case -119: {
                this.server.manager.createItem(this);
                break;
            }
            case -115: {
                GameScr.reciveImage(this, m);
                break;
            }
            case -114: {
                clan = ClanManager.getClanByName(this.nj.clan.clanName);
                if (clan != null) {
                    clan.LogClan(this);
                    break;
                }
                break;
            }
            case -113: {
                clan = ClanManager.getClanByName(this.nj.clan.clanName);
                if (clan != null) {
                    clan.requestClanInfo(this);
                }
            }
            case -112: {
                clan = ClanManager.getClanByName(this.nj.clan.clanName);
                if (clan != null) {
                    clan.requestClanMember(this);
                }
            }
            case -111: {
                clan = ClanManager.getClanByName(this.nj.clan.clanName);
                if (clan != null) {
                    clan.requestClanItem(this);
                    break;
                }
                break;
            }
            case -109: {
                GameScr.requestMapTemplate(this, m);
                break;
            }
            case -108: {
                GameScr.sendModTemplate(this, m.reader().readUnsignedByte());
                break;
            }
            case -101: {
                this.selectNhanVat(null);
                break;
            }
            case -95: {
                clan = ClanManager.getClanByName(this.nj.clan.clanName);
                if (clan != null) {
                    clan.setAlert(this, m);
                    break;
                }
                break;
            }
            case -94: {
                clan = ClanManager.getClanByName(this.nj.clan.clanName);
                if (clan != null) {
                    clan.changeClanType(this, m);
                    break;
                }
                break;
            }
            case -93: {
                clan = ClanManager.getClanByName(this.nj.clan.clanName);
                if (clan != null) {
                    clan.moveOutClan(this, m);
                    break;
                }
                break;
            }
            case -92: {
                clan = ClanManager.getClanByName(this.nj.clan.clanName);
                if (clan != null) {
                    clan.OutClan(this);
                    break;
                }
                break;
            }
            case -91: {
                clan = ClanManager.getClanByName(this.nj.clan.clanName);
                if (clan != null) {
                    clan.clanUpLevel(this);
                    break;
                }
                break;
            }
            case -90: {
                clan = ClanManager.getClanByName(this.nj.clan.clanName);
                if (clan != null) {
                    clan.inputCoinClan(this, m);
                    break;
                }
                break;
            }
            case -88: {
                GameScr.doConvertUpgrade(this, m);
                break;
            }
            case -87: {
                // Moi vao ldgt
                val name = m.reader().readUTF();
                clan = ClanManager.getClanByName(nj.clan.clanName);
                if (clan != null) {
                    clan.inviteToDun(name);
                }
                break;
            }
            case -85: {
                ItemData.divedeItem(this, m);
                break;
            }
            case -82: {
                this.rewardedCave();
                break;
            }
            case -79: {
                // Reward chienTruong
                this.rewardBattle();
                break;
            }
            case -72: {
                GameScr.LuckValue(this, m);
                break;
            }
            case -62: {
                clan = ClanManager.getClanByName(this.nj.clan.clanName);
                if (clan != null) {
                    clan.openItemLevel(this);
                    break;
                }
                break;
            }
            case -61: {
                clan = ClanManager.getClanByName(this.nj.clan.clanName);
                if (clan != null) {
                    clan.sendClanItem(this, m);
                    break;
                }
                break;
            }
            default:
                util.Debug("Not match message not map " + cmd);
        }
        m.cleanup();
    }

    private void rewardBattle() {
        final short[] rewards = server.globalBattle.getRewards(this.nj);
        if (rewards.length > 0) {
            for (int i = 1, rewardsLength = rewards.length; i < rewardsLength; i++) {
                short reward = rewards[i];
                if (reward >= 275 && reward <= 278) {
                    val item = ItemData.itemDefault(reward);
                    item.quantity = rewards[0];
                    nj.addItemBag(true, item);
                } else {
                    val item = ItemData.itemDefault(reward);
                    item.quantity = 1;
                    nj.addItemBag(false, item);
                }
            }
            nj.battleData.setPhe(Constants.PK_NORMAL);
            nj.resetPoint();
            nj.upPoint(0);
        } else {
            sendYellowMessage("Có cái nịt");
        }
    }

    public void Admission(final byte nclass) throws IOException {
        switch (nclass) {
            case 1: {
                this.nj.addItemBag(true, ItemData.itemDefault(94, true));
                this.nj.addItemBag(true, ItemData.itemDefault(40, true));
//                this.nj.addItemBag(true, ItemData.itemDefault(420, true));
                break;
            }
            case 2: {
                this.nj.addItemBag(true, ItemData.itemDefault(114, true));
                this.nj.addItemBag(true, ItemData.itemDefault(49, true));
//                this.nj.addItemBag(true, ItemData.itemDefault(420, true));
                break;
            }
            case 3: {
                this.nj.addItemBag(true, ItemData.itemDefault(99, true));
                this.nj.addItemBag(true, ItemData.itemDefault(58, true));
//                this.nj.addItemBag(true, ItemData.itemDefault(421, true));
                break;
            }
            case 4: {
                this.nj.addItemBag(true, ItemData.itemDefault(109, true));
                this.nj.addItemBag(true, ItemData.itemDefault(67, true));
//                this.nj.addItemBag(true, ItemData.itemDefault(421, true));
                break;
            }
            case 5: {
                this.nj.addItemBag(true, ItemData.itemDefault(104, true));
                this.nj.addItemBag(true, ItemData.itemDefault(76, true));
//                this.nj.addItemBag(true, ItemData.itemDefault(422, true));
                break;
            }
            case 6: {
                this.nj.addItemBag(true, ItemData.itemDefault(119, true));
                this.nj.addItemBag(true, ItemData.itemDefault(85, true));
//                this.nj.addItemBag(true, ItemData.itemDefault(422, true));
                break;
            }
        }
        final Body value = this.nj.get();
        this.nj.clan.nClass = nclass;
        value.nclass = nclass;
        this.nj.get().getSkills().clear();
        this.nj.get().upHP(this.nj.get().getMaxHP());
        this.nj.get().upMP(this.nj.get().getMaxMP());
        this.nj.get().setSpoint(Level.totalsPoint(this.nj.get().getLevel()));
        this.nj.get().updatePpoint(Level.totalpPoint(this.nj.get().getLevel()) + nj.get().getTiemNangSo() * 10 + nj.get().getBanghoa() * 10);
        this.nj.get().setPotential0(5);
        this.nj.get().setPotential1(5);
        this.nj.get().setPotential2(5);
        this.nj.get().setPotential3(10);
        final Message m = new Message(-30);
        m.writer().writeByte(-126);
        m.writer().writeByte(this.nj.get().speed());
        m.writer().writeInt(this.nj.get().getMaxHP());
        m.writer().writeInt(this.nj.get().getMaxMP());
        m.writer().writeShort(this.nj.get().getPotential0());
        m.writer().writeShort(this.nj.get().getPotential1());
        m.writer().writeInt(this.nj.get().getPotential2());
        m.writer().writeInt(this.nj.get().getPotential3());
        m.writer().writeByte(this.nj.get().nclass);
        m.writer().writeShort(this.nj.get().getSpoint());
        m.writer().writeShort(this.nj.get().getPpoint());
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    }

    @SneakyThrows
    public void removeClassItem() {

        Item[] itemBody = this.nj.get().ItemBody;

        for (int i = 0, itemBodyLength = itemBody.length; i < itemBodyLength; i++) {
            Item item = itemBody[i];
            if (item != null && (item.getData().isVuKhi() ||
                    item.getData().isTrangPhuc() ||
                    item.getData().isYoroi() ||
                    item.getData().isTrangSuc())) {
                itemBody[i] = null;
            }
        }

        for (int i = 0; i < this.nj.ItemBag.length; i++) {
            val item = this.nj.ItemBag[i];
            if (item != null && (item.getData().isVuKhi() ||
                    item.getData().isTrangPhuc() ||
                    item.getData().isYoroi() ||
                    item.getData().isTrangSuc())) {
                this.nj.ItemBag[i] = null;
            }
        }

        for (int i = 0, boxLength = this.nj.ItemBox.length; i < boxLength; i++) {
            Item item = this.nj.ItemBox[i];
            if (item != null && (item.getData().isVuKhi() ||
                    item.getData().isTrangPhuc() ||
                    item.getData().isYoroi() ||
                    item.getData().isTrangSuc())) {
                this.nj.ItemBox[i] = null;
            }
        }

        sendInfo(false);
    }

    private void plusSkillpoints(final Message m) throws IOException {
        final short sk = m.reader().readShort();
        final byte point = m.reader().readByte();
        m.cleanup();
        // Cộng điểm ki năng

        if (nj.getTaskId() == 9 && nj.getTaskIndex() == 2) {
            if (point != 0) {
                nj.upMainTask();
            }
        }

        final Skill skill = this.nj.get().getSkill(sk);
        if (skill == null || this.nj.get().getSpoint() <= 0 || point <= 0) {
            return;
        }
        int sumSkill = 0;
        for (Skill skill1 : this.nj.get().getSkills()) {
            if (skill1.id >= 67 && skill1.id <= 72)
                continue;
            sumSkill += skill1.point - 1;
        }
        if (this.nj.get().getSpoint() + sumSkill > 1.75 * (Level.totalsPoint(this.nj.get().getLevel()) + this.nj.get().getKyNangSo() + this.nj.get().getPhongLoi())) {
            session.sendMessageLog("Tài khoản bạn đã hack game và bị khoá vui lòng không ý kiến");
            lockAcc();
            return;
        }
        if (sk >= 67 && sk <= 72) {
            this.session.sendMessageLog("Không thể cộng điểm cho kĩ năng này");
            return;
        }

        final SkillData data = SkillData.Templates(sk);
        if (skill.point + point > data.maxPoint) {
            this.session.sendMessageLog("Cấp tối đa là " + data.maxPoint);
            return;
        }
        final Skill skill2 = skill;
        skill2.point += point;
        final Body value = this.nj.get();
        value.setSpoint(value.getSpoint() - point);
        this.nj.get().upHP(this.nj.get().getMaxHP());
        this.nj.get().upMP(this.nj.get().getMaxMP());
        this.loadSkill();
    }

    public void loadSkill() throws IOException {
        final Message m = new Message(-30);
        m.writer().writeByte(-125);
        m.writer().writeByte(this.nj.get().speed());
        m.writer().writeInt(this.nj.get().getMaxHP());
        m.writer().writeInt(this.nj.get().getMaxMP());
        m.writer().writeShort(this.nj.get().getSpoint());
        m.writer().writeByte(this.nj.get().getSkills().size());
        for (short i = 0; i < this.nj.get().getSkills().size(); ++i) {
            final Skill fs = this.nj.get().getSkills().get(i);
            m.writer().writeShort(SkillData.Templates(fs.id, fs.point).skillId);
        }
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    }

    public void getMp() throws IOException {
        final Message m = new Message(-30);
        m.writer().writeByte(-121);
        m.writer().writeInt(this.nj.get().mp);
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    }

    @SneakyThrows
    private void lockAcc() {
//        SQLManager.executeUpdate("UPDATE `player` set `lock`=1 where `id`=" + this.id + " limit 1;");
//        conn.disconnect();
    }

    private void pluspPoint(final Message m) throws IOException {

        // TODO Check PPOINT
        if (this.nj.get().nclass == 0) {
            sendYellowMessage("Hãy nhập học để được cộng điểm");
            return;
        }

        final byte num = m.reader().readByte();
        final short point = m.reader().readShort();
        m.cleanup();
        if (point <= 0 || point > this.nj.get().getPpoint()) {
            session.sendMessageLog("Điểm cộng không hợp lệ");
            return;
        }


        if (nj.getTaskId() == 9 && nj.getTaskIndex() == 1) {
            nj.upMainTask();
        }

        if (this.nj.get().getPpoint() +
                this.nj.get().getPotential0() +
                this.nj.get().getPotential1() +
                nj.get().getPotential2() +
                nj.get().getPotential3() >
                (Level.totalpPoint(nj.get().getLevel()) + 25 + nj.get().getTiemNangSo() * 10 + nj.get().getBanghoa() * 10)) {
            session.sendMessageLog("Lỗi cộng điểm tiềm năng tiềm năng được reset");
            restPpoint(this.nj.get());
            return;
        }

        switch (num) {
            case 0: {
                final Body value = this.nj.get();
                value.setPotential0(value.getPotential0() + point);
                break;
            }
            case 1: {
                final Body value2 = this.nj.get();
                value2.setPotential1(value2.getPotential1() + point);
                break;
            }
            case 2: {
                final Body value3 = this.nj.get();
                value3.setPotential2(value3.getPotential2() + point);
                break;
            }
            case 3: {
                final Body value4 = this.nj.get();
                value4.setPotential3(value4.getPotential3() + point);
                break;
            }
            default: {
                return;
            }
        }
        final Body b = this.nj.get();

        b.updatePpoint(b.getPpoint() - point);
        this.nj.get().upHP(this.nj.get().getMaxHP());
        this.nj.get().upMP(this.nj.get().getMaxMP());
        this.updatePotential();
    }

    private volatile long lastTimeResetPoint = -1;

    @SneakyThrows
    public synchronized void restPpoint(Body body) {
        if (lastTimeResetPoint != -1 && System.currentTimeMillis() - lastTimeResetPoint < MIN_TIME_RESET_POINT) {
            return;
        }
        body.setPotential0(5);
        body.setPotential1(5);
        body.setPotential2(5);
        body.setPotential3(10);

        body.updatePpoint(Level.totalpPoint(body.getLevel()) + 10 * (body.getTiemNangSo() + body.getBanghoa()));
        lastTimeResetPoint = System.currentTimeMillis();
        this.updatePotential();
    }


    @SneakyThrows
    public synchronized void restSpoint() {
        if (lastTimeResetPoint != -1 && System.currentTimeMillis() - lastTimeResetPoint < MIN_TIME_RESET_POINT) {
            return;
        }

        for (final Skill skill : this.nj.get().getSkills()) {
            if (skill.getTemplate().skillId == 67) continue;
            if (skill.id != 0 && skill.id != 72 && skill.id != 68) {
                skill.point = 1;
            }
        }
        this.nj.get().setSpoint(Level.totalsPoint(this.nj.get().getLevel()) + this.nj.get().getPhongLoi() + this.nj.get().getKyNangSo());
        lastTimeResetPoint = System.currentTimeMillis();
        this.loadSkill();
    }

    public void updatePotential() throws IOException {
        final Message m = new Message(-30);
        m.writer().writeByte(-109);
        m.writer().writeByte(this.nj.get().speed());
        m.writer().writeInt(this.nj.get().getMaxHP());
        m.writer().writeInt(this.nj.get().getMaxMP());
        m.writer().writeShort(this.nj.get().getPpoint());
        m.writer().writeShort(this.nj.get().getPotential0());
        m.writer().writeShort(this.nj.get().getPotential1());
        m.writer().writeInt(this.nj.get().getPotential2());
        m.writer().writeInt(this.nj.get().getPotential3());
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    }

    private void xuBagtoBox(Message m) throws IOException {
        final int xu = m.reader().readInt();
        if (xu <= 0 || xu > this.nj.xu) {
            return;
        }
        if (xu + (long) this.nj.xuBox > 2000000000L) {
            this.session.sendMessageLog("Bạn chỉ có thể cất thêm " + getFormatNumber(xu + (long) this.nj.xu - 2000000000L));
            return;
        }
        final Ninja c = this.nj;
        c.xu -= xu;
        final Ninja c2 = this.nj;
        c2.xuBox += xu;
        m = new Message(-30);
        m.writer().writeByte(-105);
        m.writer().writeInt(xu);
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    }

    private void xuBoxtoBag(Message m) throws IOException {
        final int xu = m.reader().readInt();
        if (xu <= 0 || xu > this.nj.xuBox) {
            return;
        }
        if (xu + (long) this.nj.xu > 2000000000L) {
            this.session.sendMessageLog("Bạn chỉ có thể rút thêm " + getFormatNumber(xu + (long) this.nj.xu - 2000000000L));
            return;
        }
        final Ninja c = this.nj;
        c.xu += xu;
        final Ninja c2 = this.nj;
        c2.xuBox -= xu;
        m = new Message(-30);
        m.writer().writeByte(-104);
        m.writer().writeInt(xu);
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    }

    public void openBagLevel(final byte index) throws IOException {
        final Message m = new Message(-30);
        m.writer().writeByte(-91);
        m.writer().writeByte(this.nj.ItemBag.length);
        m.writer().writeByte(index);
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    }

    @SneakyThrows
    public void viewFriend() throws IOException {
        final Message m = new Message(-30);
        m.writer().writeByte(-85);
        for (Friend friend : this.nj.friend) {
            if (friend.getAgree()) {
                final Ninja n = PlayerManager.getInstance().getNinja(friend.getName());
                if (n != null) {
                    m.writer().writeUTF(friend.getName());
                    m.writer().writeByte(3);
                } else {
                    m.writer().writeUTF(friend.getName());
                    m.writer().writeByte(1);
                }
            } else {
                m.writer().writeUTF(friend.getName());
                m.writer().writeByte(-1);
            }
        }
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    }

    protected void deleteFriend(Message m) throws IOException {
        final String nF = m.reader().readUTF();
        m.cleanup();
        this.nj.friend = this.nj.friend.stream().filter(p -> !p.getName().equals(nF)).collect(Collectors.toList());
        m = new Message(-30);
        m.writer().writeByte(-83);
        m.writer().writeUTF(nF);
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
        viewFriend();
    }

    public void acceptInviteClan(final Message m) throws IOException {
        final int charMapid = m.reader().readInt();
        m.cleanup();
        if (this.nj.clan.clanName.length() > 0) {
            this.session.sendMessageLog("Bạn đã có gia tộc.");
            return;
        }
        final Ninja n = this.nj.getPlace().getNinja(charMapid);
        if (n == null || n.requestclan != this.nj.id) {
            this.sendYellowMessage("Lời mời đã hết hạn.");
            return;
        }
        final ClanManager clan = ClanManager.getClanByName(n.clan.clanName);
        if (clan != null) {
            if (clan.members.size() >= clan.getMemMax()) {
                this.session.sendMessageLog("Gia tộc đã đầy thành viên.");
            } else if (Math.abs(this.nj.get().x - n.x) < 70 && Math.abs(this.nj.get().x - n.x) < 50) {
                this.nj.requestclan = -1;
                this.nj.clan.clanName = clan.name;
                this.nj.clan.typeclan = 0;
                clan.members.add(this.nj.clan);
                this.setTypeClan(this.nj.clan.typeclan);
            } else {
                this.sendYellowMessage("Khoảng cách quá xa không thể chấp nhận lời mời vào gia tộc");
            }
        }
    }

    public void clanInvite(Message m) throws IOException {
        final int charId = m.reader().readInt();
        m.cleanup();
        if (this.nj.requestclan != -1) {
            this.session.sendMessageLog("Bạn đã gửi lời mời tham gia gia tộc.");
            return;
        }
        final ClanManager clan = ClanManager.getClanByName(this.nj.clan.clanName);
        if (clan != null && this.nj.clan.typeclan > 1) {
            if (clan.members.size() < clan.getMemMax()) {
                final Ninja n = this.nj.getPlace().getNinja(charId);
                if (n == null) {
                    return;
                }
                if (n.requestclan != -1) {
                    this.session.sendMessageLog("Đối phương đang có lời mời vào giao tộc");
                } else if (n.clan.clanName.length() > 0) {
                    this.session.sendMessageLog("Đối phương đã có gia tộc");
                } else if (Math.abs(this.nj.get().x - n.x) < 70 && Math.abs(this.nj.get().x - n.x) < 50) {
                    this.nj.requestclan = n.id;
                    this.nj.deleyRequestClan = System.currentTimeMillis() + 10000L;
                    m = new Message(-30);
                    m.writer().writeByte(-63);
                    m.writer().writeInt(this.nj.get().id);
                    m.writer().writeUTF(this.nj.clan.clanName);
                    m.writer().flush();
                    n.p.sendMessage(m);
                    m.cleanup();
                } else {
                    this.sendYellowMessage("Khoảng cách quá xa không thể gửi lời mời vào gia tộc");
                }
            } else {
                this.session.sendMessageLog("Gia tộc đã tối đa thành viện tham gia");
            }
        }
    }

    public void setTypeClan(final int type) throws IOException {
        this.nj.clan.typeclan = (byte) type;
        final Message m = new Message(-30);
        m.writer().writeByte(-62);
        m.writer().writeInt(this.nj.id);
        m.writer().writeUTF(this.nj.clan.clanName);
        m.writer().writeByte(this.nj.clan.typeclan);
        m.writer().flush();
        this.nj.getPlace().sendMessage(m);
        m.cleanup();
    }

    public void clanPlease(Message m) throws IOException {
        final int charID = m.reader().readInt();
        m.cleanup();
        if (this.nj.clan.clanName.length() > 0) {
            this.session.sendMessageLog("Bạn đã có gia tộc");
        } else {
            final Ninja n = this.nj.getPlace().getNinja(charID);
            if (n == null || n.clan.typeclan < 2) {
                return;
            }
            final ClanManager clan = ClanManager.getClanByName(n.clan.clanName);
            if (clan == null) {
                return;
            }
            if (clan.members.size() >= clan.getMemMax()) {
                this.session.sendMessageLog("Gia tộc đã đầy thành viên.");
            } else if (this.nj.requestclan != -1) {
                this.session.sendMessageLog("Bạn đã gửi yêu cầu gia nhập biêt đội");
            } else if (Math.abs(this.nj.x - n.x) < 70 && Math.abs(this.nj.x - n.x) < 50) {
                this.nj.requestclan = n.id;
                this.nj.deleyRequestClan = System.currentTimeMillis() + 15000L;
                m = new Message(-30);
                m.writer().writeByte(-61);
                m.writer().writeInt(this.nj.get().id);
                m.writer().flush();
                n.p.sendMessage(m);
                m.cleanup();
            } else {
                this.sendYellowMessage("Khoảng cách quá xa không thể gửi yêu cầu vào gia tộc");
            }
        }
    }

    public void acceptPleaseClan(final Message m) throws IOException {
        final int charID = m.reader().readInt();
        m.cleanup();
        final ClanManager clan = ClanManager.getClanByName(this.nj.clan.clanName);
        if (clan == null || this.nj.clan.typeclan < 2) {
            return;
        }
        final Ninja n = this.nj.getPlace().getNinja(charID);
        if (n == null || n.requestclan != this.nj.id) {
            this.sendYellowMessage("Lời mời đã hết hạn.");
            return;
        }
        if (clan.members.size() >= clan.getMemMax()) {
            this.session.sendMessageLog("Gia tộc đã đầy thành viên.");
        } else if (n.clan.clanName.length() > 0) {
            this.session.sendMessageLog("Đối phương đã có gia tộc.");
        } else if (Math.abs(this.nj.get().x - n.x) < 70 && Math.abs(this.nj.get().x - n.x) < 50) {
            n.requestclan = -1;
            n.clan.clanName = clan.name;
            n.clan.typeclan = 0;
            clan.members.add(n.clan);
            n.p.setTypeClan(n.clan.typeclan);
        } else {
            this.sendYellowMessage("Khoảng cách quá xa không thể chấp nhận yêu cầu vào gia tộc");
        }
    }

    private void pasteSkill(final Message m) throws IOException {
        final String t1 = m.reader().readUTF();
        final String t2 = m.reader().readUTF();
        final short lent = m.reader().readShort();
        util.Debug("load skill");
        final String s = t1;
        switch (s) {
            case "KSkill": {
                for (byte i = 0; i < this.nj.get().KSkill.length; ++i) {
                    final byte sid = m.reader().readByte();
                    if (sid != -1) {
                        final Skill skill = this.nj.get().getSkill(sid);
                        if (skill != null && SkillData.Templates(skill.id).type != 0) {
                            this.nj.get().KSkill[i] = skill.id;
                        }
                    }
                }
                break;
            }
            case "OSkill": {
                for (byte i = 0; i < this.nj.get().OSkill.length; ++i) {
                    final byte sid = m.reader().readByte();
                    if (sid != -1) {
                        final Skill skill = this.nj.get().getSkill(sid);
                        if (skill != null && SkillData.Templates(skill.id).type != 0) {
                            this.nj.get().OSkill[i] = skill.id;
                        }
                    }
                }
                break;
            }
        }
        m.cleanup();
    }

    public void upExpClan(final int exp) {
        final ClanManager clan = ClanManager.getClanByName(this.nj.clan.clanName);
        if (clan != null && clan.getMem(this.nj.name) != null) {
            final ClanMember clan2 = this.nj.clan;
            clan2.pointClan += exp;
            final ClanMember clan3 = this.nj.clan;
            clan3.pointClanWeek += exp;
            clan.upExp(exp);
            this.sendYellowMessage("Gia tộc của Bạn nhận được " + exp + " kinh nghiệm");
        }
    }

    public void selectNhanVat(Message m) throws Exception {
        if (m != null && this.nj == null) {
            final String name = m.reader().readUTF();
            for (byte i = 0; i < this.sortNinja.length; ++i) {
                if (name.equals(this.sortNinja[i])) {
                    this.setNj(Ninja.setup(this, this.sortNinja[i]));
                    if (this.nj != null) {
                        PlayerManager.getInstance().put(this.nj);
                        this.sendInfo();
                        this.nj.sendTaskOrders();
                        m = new Message(-23);
                        m.writer().writeInt(this.nj.get().id);
                        m.writer().writeUTF("Trò chơi dành cho người từ đủ 18 tuổi chở lên. Chơi game quá 180 phút mỗi ngày có hại cho sức khỏe.");
                        m.writer().flush();
                        m.cleanup();

                        this.sendMessage(m);
                        val level = this.nj.getLevel();
                        if (nj.getTaskIndex() != -1) {
                            Service.getTask(nj);
                            TaskHandle.requestLevel(nj);
                        }
                        Tournament.getTypeTournament(level).restoreNinjaTournament(nj);
                        try {
                            this.session.setName("SESSION OF " + nj.name + " user " + username);
                        } catch (Exception e) {

                        }
                        server.manager.sendTB(this, "Baggis",
                                "Chào mừng các player đến với sever Baggis \n" +
                                        "Box zalo mới: https://zalo.me/g/hlzivr796 \n" +
                                        "Lệnh làm sự kiện : ( autosk x 0 ) và (autosk x 1) \n" +
                                        "MUA XU LƯỢNG LIÊN HÊ ZALO Lvc \n" +
                                        "Số người online " + ( + PlayerManager.getInstance().conns_size()));

                        if (this.nj != null && this.nj.clan != null) {
                            Server.clanTerritoryManager.getClanTerritoryDataById(this.getClanTerritoryId());
                        }
                        break;
                    }
                }
            }

            return;
        }
        m = new Message(-28);
        m.writer().writeByte(-126);
        byte lent = 0;
        for (byte i = 0; i < this.sortNinja.length; ++i) {
            if (this.sortNinja[i] != null) {
                ++lent;
            }
        }
        m.writer().writeByte(lent);
        for (byte j = 0; j < this.sortNinja.length; ++j) {
            if (this.sortNinja[j] != null) {
                Message finalM = m;
                SQLManager.executeQuery("SELECT `gender`,`name`,`class`,`level`,`head`,`ItemBody` FROM `ninja` WHERE `name`LIKE'" + this.sortNinja[j] + "';", (red) -> {
                    if (red != null && red.first()) {
                        finalM.writer().writeByte(red.getByte("gender"));
                        finalM.writer().writeUTF(red.getString("name"));
                        finalM.writer().writeUTF(this.server.manager.NinjaS[red.getByte("class")]);
                        finalM.writer().writeByte(red.getInt("level"));
                        short head = red.getByte("head");
                        short weapon = -1;
                        short body = -1;
                        short leg = -1;
                        final JSONArray jar = (JSONArray) JSONValue.parse(red.getString("ItemBody"));
                        final Item[] itembody = new Item[32];
                        if (jar != null) {
                            for (byte k = 0; k < jar.size(); ++k) {
                                final JSONObject job = (JSONObject) jar.get(k);
                                final byte index = Byte.parseByte(job.get("index").toString());
                                itembody[index] = ItemData.parseItem(jar.get(k).toString());
                            }
                        }
                        if (itembody[11] != null) {
                            head = ItemData.ItemDataId(itembody[11].id).part;
                            if (itembody[11].id == 541) {
                                head = 185;
                            }
                            if (itembody[11].id == 542) {
                                head = 188;
                            }
                            if (itembody[11].id == 745) {
                                head = 264;
                            }
                            if (itembody[11].id == 774) {
                                head = 267;
                            }
                            if (itembody[11].id == 786) {
                                head = 270;
                            }
                            if (itembody[11].id == 787) {
                                head = 276;
                            }
                        }
                        if (itembody[1] != null) {
                            weapon = ItemData.ItemDataId(itembody[1].id).part;
                        }
                        if (itembody[2] != null) {
                            body = ItemData.ItemDataId(itembody[2].id).part;
                        }
                        if (itembody[6] != null) {
                            leg = ItemData.ItemDataId(itembody[6].id).part;
                        }
                        if (head == 185 || head == 188 || head == 258 || head == 264 || head == 267 || head == 270 || head == 276) {
                            body = (short) (head + 1);
                            leg = (short) (head + 2);
                        }
                        finalM.writer().writeShort(head);
                        finalM.writer().writeShort(weapon);
                        finalM.writer().writeShort(body);
                        finalM.writer().writeShort(leg);
                    }

                });

            }
        }
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    }

    private void createNinja(final Message m) throws Exception {
        if (this.sortNinja[2] != null) {
            return;
        }
        final String name = m.reader().readUTF().toLowerCase();
        final byte gender = m.reader().readByte();
        final byte head = m.reader().readByte();
        m.cleanup();
        if (!CheckString(name, "^[a-zA-Z0-9]+$") || name.length() < 5 || name.length() > 15) {
            this.session.sendMessageLog("Tên nhân chỉ đồng ý các ký tự a-z,0-9 và chiều dài từ 5 đến 15 ký tự");
            return;
        }
        if (this.sortNinja[0] != null) {
            this.session.sendMessageLog("Để tránh nhiều ác clone không tạo thêm nhân vật");
            return;
        }

        final boolean[] canNext = {true};
        SQLManager.executeQuery("SELECT `id` FROM `ninja` WHERE `name`LIKE'" + name + "';", (red) -> {
            try {
                if (red != null && red.first()) {
                    this.session.sendMessageLog("Tên nhân vật đã tồn tại!");
                    canNext[0] = false;
                }

            } catch (Exception e) {

            }

        });

        if (!canNext[0]) {
            return;
        }
        SQLManager.executeUpdate("INSERT INTO ninja(`name`,`gender`,`head`,`ItemBag`,`ItemBox`,`ItemBody`,`ItemMounts`, `friend`, `effect`, `clan`, `exptype`, `skill`) VALUES " +
                "(\"" + name + "\"," + gender + "," + head + ",'[]','[]','[]','[]', '[]', '[]','[]', 1, '[{\"id\": 0, \"point\": 0}]');");
        for (byte i = 0; i < this.sortNinja.length; ++i) {
            if (this.sortNinja[i] == null) {
                this.sortNinja[i] = name;
                break;
            }
        }

        this.flush();
        this.selectNhanVat(null);
    }

    public void sendYellowMessage(final String str) {
        try {
            final Message m = new Message(-24);
            m.writer().writeUTF(str);
            m.writer().flush();
            this.sendMessage(m);
            m.cleanup();
        } catch (IOException ex) {
        }
    }

    public void privateChat(Message m) throws IOException {
        final String name = m.reader().readUTF();
        final String chat = m.reader().readUTF();
        final Ninja n = PlayerManager.getInstance().getNinja(name);
        if (n == null || n.id == this.nj.id) {
            return;
        }
        m = new Message(-22);
        m.writer().writeUTF(this.nj.name);
        m.writer().writeUTF(chat);
        m.writer().flush();
        n.p.sendMessage(m);
        m.cleanup();
    }

    public void luongMessage(final long luongup) {
        this.upluong(luongup);
        try {
            final Message m = new Message(-30);
            m.writer().writeByte(-72);
            m.writer().writeInt(this.luong);
            m.writer().flush();
            this.sendMessage(m);
            m.cleanup();
        } catch (IOException ex) {
        }
    }

    public void upluongMessage(final long luongup) {
        try {
            final Message m = new Message(-30);
            m.writer().writeByte(-71);
            m.writer().writeInt(this.upluong(luongup));
            m.writer().flush();
            this.sendMessage(m);
            m.cleanup();
        } catch (IOException ex) {
        }
    }

    @SneakyThrows
    public void removeLuong(long luong) {
        if (luong < 0) throw new RuntimeException("Luong must >=0");
        upluong(-luong);
        final Message m = new Message(-30);
        m.writer().writeByte(56 - 128);
        m.writer().writeInt(this.luong);
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    }


    public static long DIFFER_USE_ITEM_TIME;
    private int useItemCount = 0;
    public static int MAX_USE_ITEM_FAST = 500;


    public void useItem(final Message m) throws IOException {
        val differ = System.currentTimeMillis() - this.lastTimeUseItem;
        util.Debug(differ + "");
        //if (differ < DIFFER_USE_ITEM_TIME) {
            //useItemCount++;
        //} else {
            //useItemCount = 0;
        //}
        //this.lastTimeUseItem = System.currentTimeMillis();

        //if (useItemCount >= MAX_USE_ITEM_FAST) {
            //session.disconnect();
            //PlayerManager.getInstance().kickSession(session);
            //return;
        //}

        final byte index = m.reader().readByte();
        m.cleanup();
        final Item item = this.nj.getIndexBag(index);
        if (item == null) {
            return;
        }
        useItem.uesItem(this, item, index);
    }

    private synchronized void setMoney(final int sxu, final int syen, final int sluong) {
        this.nj.xu = sxu;
        this.nj.yen = syen;
        this.luong = sluong;
        try {
            final Message m = new Message(13);
            m.writer().writeInt(this.nj.xu);
            m.writer().writeInt(this.nj.yen);
            m.writer().writeInt(this.luong);
            m.writer().flush();
            this.sendMessage(m);
            m.cleanup();
        } catch (IOException ex) {
        }
    }

    public void SellItemBag(Message m) throws IOException {
        final int index = m.reader().readUnsignedByte();
        int num = 1;
        if (m.reader().available() > 0) {
            num = m.reader().readInt();
        }
        m.cleanup();
        final Item item = this.nj.getIndexBag(index);
        if (item == null || (ItemData.ItemDataId(item.id).isUpToUp && (num <= 0 || num > item.quantity))) {
            return;
        }
        if (ItemData.ItemDataId(item.id).isUpToUp) {
            num = 1;
        }
        if (ItemData.isTypeBody(item.id) && item.getUpgrade() > 0) {
            this.session.sendMessageLog("Không thể bán trang bị còn nâng cấp");
            return;
        }
        final ItemData data = ItemData.ItemDataId(item.id);
        if (data.type == 12) {
            this.session.sendMessageLog("Vật phẩm quý giá bạn không thể bán được");
            return;
        }
        final Item item2 = item;
        item2.quantity -= num;
        if (item.quantity <= 0) {
            this.nj.ItemBag[index] = null;
        }
        this.nj.upyen(item.sale * num);
        m = new Message(14);
        m.writer().writeByte(index);
        m.writer().writeInt(this.nj.yen);
        m.writer().writeShort(num);
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    }

    public void itemBodyToBag(Message m) throws IOException {
        final byte index = m.reader().readByte();
        m.cleanup();
        final int idItemBag = this.nj.getIndexBagNotItem();
        if (idItemBag == -1) {
            this.session.sendMessageLog("Hành trang không đủ chỗ trống");
            return;
        }
        if (index < 0 && index >= this.nj.get().ItemBody.length) {
            return;
        }
        final Item itembody = this.nj.get().ItemBody[index];
        this.nj.ItemBag[idItemBag] = itembody;
        this.nj.get().ItemBody[index] = null;
        if (itembody.id == 569) {
            this.removeEffect(36);
        }
        if (index == 10) {
            this.mobMeMessage(0, (byte) 0);
        }
        moveItemBackToBag(index, idItemBag);
        Service.CharViewInfo(this, false);
    }

    public void moveItemBackToBag(byte index, int idItemBag) throws IOException {
        Message m;
        m = new Message(15);
        m.writer().writeByte(this.nj.get().speed());
        m.writer().writeInt(this.nj.get().getMaxHP());
        m.writer().writeInt(this.nj.get().getMaxMP());
        m.writer().writeShort(this.nj.get().eff5buffHP());
        m.writer().writeShort(this.nj.get().eff5buffMP());
        m.writer().writeByte(index);
        m.writer().writeByte(idItemBag);
        m.writer().writeShort(this.nj.get().partHead());
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    }

    public void itemBoxToBag(Message m) throws IOException {
        final byte index = m.reader().readByte();
        m.cleanup();
        final Item item = this.nj.getIndexBox(index);
        if (item == null) {
            return;
        }
        final ItemData data = ItemData.ItemDataId(item.id);
        int indexBag = this.nj.getIndexBagid(item.id, item.isLock());
        if (!item.isExpires && data.isUpToUp && indexBag != -1) {
            this.nj.ItemBox[index] = null;
            final Item item2 = this.nj.ItemBag[indexBag];
            item2.quantity += item.quantity;
        } else {
            if (this.nj.getAvailableBag() <= 0) {
                this.session.sendMessageLog("Rương đồ không đủ chỗ trống");
                return;
            }
            indexBag = this.nj.getIndexBagNotItem();
            this.nj.ItemBox[index] = null;
            this.nj.ItemBag[indexBag] = item;
        }
        m = new Message(16);
        m.writer().writeByte(index);
        m.writer().writeByte(indexBag);
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    }

    public void itemBagToBox(Message m) throws IOException {
        final byte index = m.reader().readByte();
        m.cleanup();
        final Item item = this.nj.getIndexBag(index);
        if (item == null) {
            return;
        }
        final ItemData data = ItemData.ItemDataId(item.id);
        byte indexBox = this.nj.getIndexBoxid(item.id, item.isLock());
        if (!item.isExpires && data.isUpToUp && indexBox != -1) {
            this.nj.ItemBag[index] = null;
            final Item item2 = this.nj.ItemBox[indexBox];
            item2.quantity += item.quantity;
        } else {
            if (this.nj.getBoxNull() <= 0) {
                this.session.sendMessageLog("Rương đồ không đủ chỗ trống");
                return;
            }
            indexBox = this.nj.getIndexBoxNotItem();
            this.nj.ItemBag[index] = null;
            this.nj.ItemBox[indexBox] = item;
        }
        m = new Message(17);
        m.writer().writeByte(index);
        m.writer().writeByte(indexBox);
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    }

    public void openUI(final int typeUI) throws IOException {
        final Message m = new Message(30);
        m.writer().writeByte(typeUI);
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    }

    public void requestItemInfoMessage(final Item item, final int index, final int typeUI) throws IOException {
        final Message m = new Message(42);
        m.writer().writeByte(typeUI);
        m.writer().writeByte(index);
        m.writer().writeLong(item.expires);
        if (ItemData.isTypeUIME(typeUI)) {
            m.writer().writeInt(item.sale);
        }
        if (ItemData.isTypeUIShop(typeUI) || ItemData.isTypeUIShopLock(typeUI) || ItemData.isTypeMounts(typeUI) || ItemData.isTypeUIStore(typeUI) || ItemData.isTypeUIBook(typeUI) || ItemData.isTypeUIFashion(typeUI) || ItemData.isTypeUIClanShop(typeUI)) {
            m.writer().writeInt(item.buyCoin);
            m.writer().writeInt(item.buyCoinLock);
            m.writer().writeInt(item.buyGold);
        }
        if (ItemData.isTypeBody(item.id) || ItemData.isTypeMounts(item.id) || ItemData.isTypeNgocKham(item.id)) {
            m.writer().writeByte(item.sys);
            if (item.option != null) {
                for (final Option Option : item.option) {
                    m.writer().writeByte(Option.id);
                    m.writer().writeInt(Option.param);
                }
            }
            try {
                if (item.ngocs != null && item.ngocs.size() > 0) {

                    Option op = null;
                    if (item.getData().type == 1) {
                        op = ItemData.VU_KHI_OPTION;
                    } else if (item.getData().isTrangSuc()) {
                        op = ItemData.TRANG_SUC_OPTION;
                    } else if (item.getData().isTrangPhuc()) {
                        op = ItemData.TRANG_BI_OPTION;
                    }

                    if (op != null) {
                        for (Item ngoc : item.ngocs) {
                            if (ngoc != null) {
                                val indx = ngoc.option.indexOf(op);

                                final Option op1 = ngoc.option.get(indx + 1);
                                final Option op2 = ngoc.option.get(indx + 2);

                                if (ngoc.id == ItemData.HUYEN_TINH_NGOC) {
                                    m.writer().writeByte(109);
                                } else if (ngoc.id == ItemData.HUYET_NGOC) {
                                    m.writer().writeByte(110);
                                } else if (ngoc.id == ItemData.LAM_TINH_NGOC) {
                                    m.writer().writeByte(111);
                                } else if (ngoc.id == ItemData.LUC_NGOC) {
                                    m.writer().writeByte(112);
                                }

                                m.writer().writeInt(0);
                                m.writer().writeByte(op1.id);
                                m.writer().writeInt(op1.param);
                                m.writer().writeByte(op2.id);
                                m.writer().writeInt(op2.param);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (item.id == 233 || item.id == 234 || item.id == 235) {
            final ByteArrayOutputStream a = GameScr.loadFile("res/icon/1/diado.png");
            if (a != null) {
                final byte[] ab = a.toByteArray();
                m.writer().writeInt(ab.length);
                m.writer().write(ab);
            }
        }

        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    }

    public void requestItemInfo(final Message m) throws IOException {
        final byte type = m.reader().readByte();
        final int index = m.reader().readUnsignedByte();
        Debug("type " + type + " index" + index);
        m.cleanup();
        Item item = null;
        switch (type) {
            case 2: {
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            }
            case 3: {
                if (index < 0 || index >= this.nj.maxluggage) {
                    return;
                }
                item = this.nj.ItemBag[index];
                break;
            }
            case 4: {
                if (index < 0 || index >= 30) {
                    return;
                }
                item = this.nj.ItemBox[index];
                break;
            }
            case 5: {
                if (index < 0 || index > 32) {
                    return;
                }
                item = this.nj.get().ItemBody[index];
                break;
            }
            case 8: {
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            }
            case 9: {
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            }
            case 14: {
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            }
            case 15: {
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            }
            case 16: {
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            }
            case 17: {
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            }
            case 18: {
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            }
            case 19: {
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            }
            case 20: {
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            }
            case 21: {
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            }
            case 22: {
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            }
            case 23: {
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            }
            case 24: {
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            }
            case 25: {
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            }
            case 26: {
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            }
            case 27: {
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            }
            case 28: {
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            }
            case 29: {
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            }
            case 32: {
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            }
            case 34: {
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            }
            case 39: {
                final ClanManager clan = ClanManager.getClanByName(this.nj.clan.clanName);
                if (clan != null && index >= 0 && index < clan.items.size()) {
                    item = clan.items.get(index);
                    break;
                }
                break;
            }
            case 41: {
                if (index < 0 || index > 4) {
                    return;
                }
                item = this.nj.ItemMounts[index];
                break;
            }
        }
        if (item == null) {
            return;
        }
        this.requestItemInfoMessage(item, index, type);
    }

    @SneakyThrows
    public void requestItemShinwaInfo(final Message m) {
        val itemId = m.reader().readInt();
        m.cleanup();
        final ItemShinwaManager.ItemShinwa itemShinwa = ItemShinwaManager.findItemById(itemId);
        if (itemShinwa == null) return;
        val mes = new Message(-152);
        mes.writer().writeInt(itemId);
        mes.writer().writeInt(itemShinwa.getItem().sale);
        val item = itemShinwa.getItem();
        if (item.isTypeBody() || item.isTypeNgocKham()) {
            mes.writer().writeByte(item.getUpgrade());
            mes.writer().writeByte(item.sys);
            for (Option option : item.option) {
                mes.writer().writeByte(option.id);
                mes.writer().writeInt(option.param);
            }
        }
        mes.writer().flush();
        sendMessage(mes);
        mes.cleanup();

    }


    public void requestTrade(Message m) throws IOException {
        final int ids = m.reader().readInt();
        m.cleanup();
        final User p = this.nj.getPlace().getNinja(ids).p;
        if (p == null) {
            this.sendYellowMessage("Người này không ở cùng khu hoặc đã offline.");
        } else if (Math.abs(this.nj.get().x - p.nj.get().x) > 100 || Math.abs(this.nj.get().y - p.nj.get().y) > 100) {
            this.sendYellowMessage("Khoảng cách quá xa.");
        } else if (this.nj.tradeDelay > System.currentTimeMillis()) {
            this.session.sendMessageLog("Bạn còn " + (this.nj.tradeDelay - System.currentTimeMillis()) / 1000L + "s để tiếp tục giao dịch.");
        } else if (this.nj.rqTradeId > 0) {
            this.session.sendMessageLog(p.nj.name + " đang có yêu cầu giao dịch.");
        } else if (p.nj.isTrade) {
            this.session.sendMessageLog(p.nj.name + " đang thực hiện giao dịch.");
        } else {
            this.nj.tradeDelay = System.currentTimeMillis() + 30000L;
            p.nj.rqTradeId = this.nj.get().id;
            m = new Message(43);
            m.writer().writeInt(this.nj.get().id);
            m.writer().flush();
            p.sendMessage(m);
            m.cleanup();
        }
    }

    public void startTrade(Message m) throws IOException {
        final int ids = m.reader().readInt();
        m.cleanup();
        if (this.nj.isTrade) {
            this.session.sendMessageLog("Bạn đã có giao dịch.");
            return;
        }
        final User p = this.nj.getPlace().getNinja(ids).p;
        if (p == null) {
            this.sendYellowMessage("Người này không ở cùng khu hoặc đã offline.");
        } else if (Math.abs(this.nj.get().x - p.nj.get().x) > 100 || Math.abs(this.nj.get().y - p.nj.get().y) > 100) {
            this.sendYellowMessage("Khoảng cách quá xa.");
        } else {
            if (!p.nj.isTrade) {
                p.nj.isTrade = true;
                p.nj.tradeId = this.nj.id;
                p.nj.tradeLock = 0;
                this.nj.isTrade = true;
                this.nj.tradeId = p.nj.id;
                this.nj.tradeLock = 0;
                this.nj.rqTradeId = 0;
                m = new Message(37);
                m.writer().writeUTF(p.nj.name);
                m.writer().flush();
                this.sendMessage(m);
                m.cleanup();
                m = new Message(37);
                m.writer().writeUTF(this.nj.name);
                m.writer().flush();
                p.sendMessage(m);
                m.cleanup();
                return;
            }
            this.session.sendMessageLog(p.nj.name + " đã có giao dịch.");
        }
        this.nj.rqTradeId = 0;
    }

    public void lockTrade(Message m) throws IOException {
        if (this.nj.tradeLock == 0) {
            final Ninja c = this.nj;
            ++c.tradeLock;
            final Ninja n = this.nj.getPlace().getNinja(this.nj.tradeId);
            if (n == null) {
                this.closeLoad();
                return;
            }
            final int tradexu = m.reader().readInt();
            if (tradexu > 0 && tradexu <= this.nj.xu) {
                this.nj.tradeCoin = tradexu;
            }
            for (byte lent = m.reader().readByte(), i = 0; i < lent; ++i) {
                final byte index = m.reader().readByte();
                final Item item = this.nj.getIndexBag(index);
                if (this.nj.tradeIdItem.size() > 12) {
                    break;
                }
                if (item != null && !item.isLock()) {
                    this.nj.tradeIdItem.add(index);
                }
            }
            if (this.nj.tradeIdItem.size() > n.getAvailableBag()) {
                this.closeLoad();
                return;
            }
            m.cleanup();
            m = new Message(45);
            m.writer().writeInt(this.nj.tradeCoin);
            m.writer().writeByte(this.nj.tradeIdItem.size());
            for (byte i = 0; i < this.nj.tradeIdItem.size(); ++i) {
                final Item item2 = this.nj.getIndexBag(this.nj.tradeIdItem.get(i));
                if (item2 != null) {
                    m.writer().writeShort(item2.id);
                    if (ItemData.isTypeBody(item2.id) || ItemData.isTypeNgocKham(item2.id)) {
                        m.writer().writeByte(item2.getUpgrade());
                    }
                    m.writer().writeBoolean(item2.isExpires);
                    m.writer().writeShort(item2.quantity);
                }
            }
            m.writer().flush();
            n.p.sendMessage(m);
            m.cleanup();
        }
    }

    public void agreeTrade() throws IOException {
        if (this.nj.tradeLock == 1) {
            final Ninja n = this.nj.getPlace().getNinja(this.nj.tradeId);
            if (n == null) {
                this.closeLoad();
                return;
            }
            final Ninja c = this.nj;
            ++c.tradeLock;
            Message m = new Message(46);
            m.writer().flush();
            n.p.sendMessage(m);
            m.cleanup();
            if (n.tradeLock == 2) {
                m = new Message(57);
                m.writer().flush();
                this.sendMessage(m);
                n.p.sendMessage(m);
                m.cleanup();
                if (n.tradeCoin > 0) {
                    n.upxuMessage(-n.tradeCoin);
                    this.nj.upxuMessage(n.tradeCoin);
                }
                if (this.nj.tradeCoin > 0) {
                    this.nj.upxuMessage(-this.nj.tradeCoin);
                    n.upxuMessage(this.nj.tradeCoin);
                }
                final ArrayList<Item> item1 = new ArrayList<Item>();
                final ArrayList<Item> item2 = new ArrayList<Item>();
                for (byte i = 0; i < n.tradeIdItem.size(); ++i) {
                    final Item item3 = n.p.nj.getIndexBag(n.tradeIdItem.get(i));
                    if (item3 != null) {
                        // TODO
                        item1.add(item3);
                        n.removeItemBag(n.tradeIdItem.get(i));
                    }
                }
                for (byte i = 0; i < this.nj.tradeIdItem.size(); ++i) {
                    final Item item3 = this.nj.getIndexBag(this.nj.tradeIdItem.get(i));
                    if (item3 != null) {
                        item2.add(item3);
                        this.nj.removeItemBag(this.nj.tradeIdItem.get(i));
                    }
                }
                for (byte i = 0; i < item1.size(); ++i) {
                    final Item item3 = item1.get(i);
                    if (item3 != null) {
                        this.nj.addItemBag(true, item3);
                    }
                }
                for (byte i = 0; i < item2.size(); ++i) {
                    final Item item3 = item2.get(i);
                    if (item3 != null) {
                        n.addItemBag(true, item3);
                    }
                }
                this.closeTrade();
                n.p.closeTrade();
            }
        }
    }

    public void closeTrade() {
        if (this.nj.isTrade) {
            this.nj.isTrade = false;
            this.nj.tradeCoin = 0;
            this.nj.tradeIdItem.clear();
            this.nj.tradeLock = -1;
            this.nj.tradeDelay = 0L;
            this.nj.tradeId = 0;
        } else if (this.nj.rqTradeId > 0) {
            this.nj.rqTradeId = 0;
        }
        this.nj.requestclan = -1;
    }

    public void closeLoad() throws IOException {
        if (this.nj.isTrade) {
            final Ninja n = PlayerManager.getInstance().getNinja(this.nj.tradeId);
            if (n != null && n.p != null && n.isTrade) {
                n.p.closeTrade();
            }
            this.closeTrade();
        }
        final Message m = new Message(57);
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    }

    public void liveFromDead() throws IOException {
        this.nj.hp = this.nj.getMaxHP();
        this.nj.mp = this.nj.getMaxMP();
        this.nj.isDie = false;
        Message m = new Message(-10);
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
        m = new Message(88);
        m.writer().writeInt(this.nj.id);
        m.writer().writeShort(this.nj.x);
        m.writer().writeShort(this.nj.y);
        m.writer().flush();
        this.nj.getPlace().sendMyMessage(this, m);
        m.cleanup();
    }

    public void viewPlayerMessage(String playername) throws IOException {

        if (playername.equals(this.nj.name)) {
            this.viewInfoPlayers(this);
            return;
        }
        Ninja n;
        if (playername.equals(this.nj.name)) {
            n = this.nj;
        } else {
            n = PlayerManager.getInstance().getNinja(playername);
        }
        if (n == null) {
            this.sendYellowMessage("Hiện tại người chơi đã offline");
            return;
        }
        n.p.sendYellowMessage(this.nj.name + " đang đứng nhìn bạn");
        this.viewInfoPlayers(n.p);
    }

    public void viewInfoPlayers(final User p) throws IOException {
        final Message m = new Message(93);
        m.writer().writeInt(p.nj.get().id);
        m.writer().writeUTF(p.nj.name);
        m.writer().writeShort(p.nj.get().partHead());
        m.writer().writeByte(p.nj.gender);
        m.writer().writeByte(p.nj.get().nclass);
        m.writer().writeByte(p.nj.get().pk);
        m.writer().writeInt(p.nj.get().hp);
        m.writer().writeInt(p.nj.get().getMaxHP());
        m.writer().writeInt(p.nj.get().mp);
        m.writer().writeInt(p.nj.get().getMaxMP());
        m.writer().writeByte(p.nj.get().speed());
        m.writer().writeShort(p.nj.get().ResFire());
        m.writer().writeShort(p.nj.get().ResIce());
        m.writer().writeShort(p.nj.get().ResWind());
        m.writer().writeInt(p.nj.get().dameMax());
        m.writer().writeInt(p.nj.get().dameDown());
        m.writer().writeShort(p.nj.get().Exactly());
        m.writer().writeShort(p.nj.get().Miss());
        m.writer().writeShort(p.nj.get().Fatal());
        m.writer().writeShort(p.nj.get().ReactDame());
        m.writer().writeShort(p.nj.get().sysUp());
        m.writer().writeShort(p.nj.get().sysDown());
        m.writer().writeByte(p.nj.get().getLevel());
        m.writer().writeShort(38);
        m.writer().writeUTF(p.nj.clan.clanName);
        if (!p.nj.clan.clanName.isEmpty()) {
            m.writer().writeByte(p.nj.clan.typeclan);
        }

        // Diem hoạt động
        m.writer().writeShort(39);
        m.writer().writeShort(40);
        m.writer().writeShort(41);
        m.writer().writeShort(42);
        m.writer().writeShort(43);
        m.writer().writeShort(44);
        m.writer().writeShort(45);
        m.writer().writeShort(46);
        m.writer().writeShort(47);
        m.writer().writeShort(48);
        m.writer().writeShort(49);
        // NV HANG NGAY
        // Finish Day
        m.writer().writeByte(p.nj.nvhnCount);
        // Nhiem vu ta thu
        // Count loop boss
        m.writer().writeByte(p.nj.taThuCount);
        m.writer().writeByte(p.nj.nCave);
        // Tiem nang sơ
        m.writer().writeByte(p.nj.get().getTiemNangSo());
        // Ky năng sơ
        m.writer().writeByte(p.nj.get().getKyNangSo());

        for (final Item body : p.nj.get().ItemBody) {
            if (body != null) {
                m.writer().writeShort(body.id);
                m.writer().writeByte(body.getUpgrade());
                m.writer().writeByte(body.sys);
            } else {
                m.writer().writeShort(-1);
            }
        }

        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();

        val mes = new Message(-155);
        val ds = mes.writer();
        ds.writeInt(0);
        ds.writeByte(nj.get().getPhongLoi());
        ds.writeByte(nj.get().getBanghoa());
        ds.flush();
        sendMessage(mes);
        m.cleanup();Service.CharViewInfo(p, false);
    }

    public void viewOptionPlayers(Message m) throws IOException {
        final int pid = m.reader().readInt();
        final byte index = m.reader().readByte();
        m.cleanup();
        final Ninja n = PlayerManager.getInstance().getNinja(pid);
        if (n == null || index < 0 || index > 15) {
            return;
        }
        final Item item = n.get().ItemBody[index];
        if (item != null) {
            m = new Message(94);
            m.writer().writeByte(index);
            m.writer().writeLong(item.expires);
            m.writer().writeInt(item.sale);
            m.writer().writeByte(item.sys);
            for (short i = 0; i < item.option.size(); ++i) {
                m.writer().writeByte(item.option.get(i).id);
                m.writer().writeInt(item.option.get(i).param);
            }
            m.writer().flush();
            this.sendMessage(m);
            m.cleanup();
        }
    }

    public void endLoad(final boolean canvas) throws IOException {
        final Message m = new Message(126);
        m.writer().writeByte(canvas ? 0 : -1);
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    }

    public void addFriend(Message m) throws IOException {
        final String nF = m.reader().readUTF();
        m.cleanup();
        if (nF.equals(this.nj.name)) {
            this.sendYellowMessage("Không thể thêm chính bản thân vào danh sách bạn bè.");
            return;
        }
        val other = PlayerManager.getInstance().getNinja(nF);
        if (other == null) {
            this.sendYellowMessage("Hiện tại người chơi này không online.");
            return;
        }
        final User otherPlayer = other.p;

        if (otherPlayer == null) {
            this.sendYellowMessage("Hiện tại người chơi này không online.");
            return;
        }
        if (this.nj.friend.stream().anyMatch(f -> f.getName().equals(nF))) {
            this.sendYellowMessage(nF + " đã có tên trong danh sách bạn bè hoặc thù địch.");
            return;
        }


        AtomicBoolean agree = new AtomicBoolean(false);
        other.friend.stream()
                .filter(f -> f != null && this.nj != null &&
                        this.nj.name.equals(f.getName()))
                .findFirst()
                .ifPresent(f -> {
                    agree.set(true);
                    f.setAgree(agree.get());
                    if (this.nj.friend == null) {
                        this.nj.friend = new ArrayList<>();
                    }

                    this.nj.friend.add(Friend.builder()
                            .name(other.name)
                            .agree(true)
                            .build());
                });

        if (!agree.get()) {
            this.nj.friend.add(Friend.builder()
                    .name(nF)
                    .agree(false)
                    .build());
        }

        if (!agree.get()) {
            m = new Message(59);
            m.writer().writeUTF(this.nj.name);
            m.writer().flush();
            otherPlayer.sendMessage(m);
            m.cleanup();
            this.sendYellowMessage("Bạn đã thêm " + nF + " vào danh sách bạn bè.");
            if (nj.getTaskId() == 11 && nj.getTaskIndex() == 1) {
                nj.upMainTask();
            }
        } else {
            otherPlayer.sendYellowMessage(this.nj.name + " đã trở thành bạn bè hữu hảo.");
            this.sendYellowMessage(nF + " đã trở thành bạn bè hữu hảo.");
        }
        viewFriend();
    }

    public void itemMonToBag(Message m) throws IOException {
        final byte index = m.reader().readByte();
        m.cleanup();
        if (index == -1) return;

        final int indexItemBag = this.nj.getIndexBagNotItem();
        if (indexItemBag == 0) {
            this.session.sendMessageLog("Hành trang không đủ chỗ trống");
            return;
        }
        if (index > 4 || index < 0 || this.nj.get().ItemMounts[index] == null) {
            return;
        }
        if (index == 4 && (this.nj.get().ItemMounts[0] != null || this.nj.get().ItemMounts[1] != null || this.nj.get().ItemMounts[2] != null || this.nj.get().ItemMounts[3] != null)) {
            this.session.sendMessageLog("Cần phải tháo hết trang bị thú cưới ra trước");
            return;
        }
        this.nj.ItemBag[indexItemBag] = this.nj.get().ItemMounts[index];
        this.nj.get().ItemMounts[index] = null;
        m = new Message(108);
        m.writer().writeByte(this.nj.get().speed());
        m.writer().writeInt(this.nj.get().getMaxHP());
        m.writer().writeInt(this.nj.get().getMaxMP());
        m.writer().writeShort(this.nj.get().eff5buffHP());
        m.writer().writeShort(this.nj.get().eff5buffMP());
        m.writer().writeByte(index);
        m.writer().writeByte(indexItemBag);
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
        for (final User user : this.nj.getPlace().getUsers()) {
            this.nj.getPlace().sendMounts(this.nj.get(), user);
        }
        Service.CharViewInfo(this, false);
    }

    public void changePassword() {
        if (!CheckString(this.passnew + this.passold, "^[a-zA-Z0-9]+$") || this.passnew.length() < 1 || this.passnew.length() > 30) {
            this.session.sendMessageLog("Mật khẩu chỉ đồng ý các ký tự a-z,0-9 và chiều dài từ 1 đến 30 ký tự");
            return;
        }
        try {

            final boolean[] canNext = {true};
            SQLManager.executeQuery("SELECT `id` FROM `player` WHERE (`password`LIKE'" + this.passold + "' AND `id` = " + this.id + ");", (red) -> {
                try {
                    if (red == null || !red.first()) {
                        this.session.sendMessageLog("Mật khẩu cũ không chính xác!");
                        canNext[0] = false;
                    }
                } catch (Exception e) {

                }
            });

            if (!canNext[0]) {
                return;
            }


            SQLManager.executeUpdate("UPDATE `player` SET `password`='" + this.passnew + "' WHERE `id`=" + this.id + " LIMIT 1;");
            this.session.sendMessageLog("Đã đổi mật khẩu thành công");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void flush() {
        final JSONArray jarr = new JSONArray();
        final byte i = 0;
        try {
            if (this.nj != null) {
                this.nj.flush();
                final String n = this.sortNinja[0];
                this.sortNinja[0] = this.nj.name;
                for (byte k = 1; k < this.sortNinja.length; ++k) {
                    if (this.sortNinja[k] != null && this.sortNinja[k].equals(this.nj.name)) {
                        this.sortNinja[k] = n;
                    }
                }
            }
            for (byte j = 0; j < this.sortNinja.length; ++j) {
                if (this.sortNinja[j] != null) {
                    jarr.add(this.sortNinja[j]);
                }
            }

            SQLManager.executeUpdate("UPDATE `player` SET `luong`=" + this.luong + ",`ninja`='" + jarr.toJSONString() + "' WHERE `id`=" + this.id + " LIMIT 1;");
            SQLManager.executeUpdate("UPDATE `player` SET `clanTerritoryId`=" + this.getClanTerritoryId() + " WHERE `id`=" + this.id + " LIMIT 1;");


        } catch (SQLException e) {
            Debug("Flush data User + Ninja Error");
            e.printStackTrace();
        }
    }

    protected void close() {
    }

    public void openBookSkill(final byte index, final byte sid) throws IOException {
        if (this.nj.get().getSkill(sid) != null) {
            this.sendYellowMessage("Bạn đã học kĩ năng này rồi");
            return;
        }
        this.nj.ItemBag[index] = null;
        final Skill skill = new Skill();
        skill.id = sid;
        skill.point = 1;
        this.nj.get().getSkills().add(skill);
        this.viewInfoPlayers(this);
        this.loadSkill();
        final Message m = new Message(-30);
        m.writer().writeByte(-102);
        m.writer().writeByte(index);
        m.writer().writeShort(SkillData.Templates(skill.id, skill.point).skillId);
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    }


    public synchronized void updateExp(long xpup, boolean useMulti) throws IOException {

        if (useMulti) {
            xpup *= Manager.MULTI_EXP;
        }

        if (xpup < 0) {
            xpup = 0;
        }

        if ((this.nj.get().getTypepk() == Constants.PK_NORMAL && this.nj.get().exptype == 0)) {
            return;
        }

        if (this.nj.get().expdown > 0L) {
            upExpDown(xpup);
        } else {
            if (this.nj.nclass > 0) {
                final Skill skill = this.nj.getSkill(66 + this.nj.nclass);
                if (skill != null && xpup >= 500000L && !this.nj.isNhanban && this.nj.clone.isIslive()) {
                    final SkillData data = SkillData.Templates(skill.id);
                    if (data.maxPoint > skill.point && nextInt(50 * skill.point) == 0) {
                        ++skill.point;
                        this.sendYellowMessage(data.name + " đã đạt cấp " + skill.point);
                        this.loadSkill();
                    }
                }
            }
            this.nj.get().expdown = 0L;
            final long xpold = this.nj.get().getExp();

//            if (xpold >= 10711676205700L) {
//                xpup = 0;
//            }

            final Body body = this.nj.get();
            body.setExp(body.getExp() + xpup);
            final int oldLv = this.nj.get().getLevel();
            this.nj.get().setLevel_Exp(this.nj.get().getExp());

            if (this.nj.get().getLevel() > 150) {
                this.nj.get().setLevel(150);
                this.nj.get().setExp(xpold);
                xpup = 0;
            }

            if (oldLv < this.nj.get().getLevel()) {
                if (this.nj.get().nclass != 0) {

                    for (int i = oldLv + 1; i <= this.nj.get().getLevel(); ++i) {
                        body.updatePpoint(body.getPpoint() + Level.getLevel(i).ppoint);
                        body.setSpoint(body.getSpoint() + Level.getLevel(i).spoint);
                    }
                } else {
                    for (int i = oldLv + 1; i <= this.nj.get().getLevel(); ++i) {
                        final Body value5 = this.nj.get();
                        value5.setPotential0(value5.getPotential0() + 5);
                        final Body value6 = this.nj.get();
                        value6.setPotential1(value6.getPotential1() + 2);
                        final Body value7 = this.nj.get();
                        value7.setPotential2(value7.getPotential2() + 2);
                        final Body value8 = this.nj.get();
                        value8.setPotential3(value8.getPotential3() + 2);
                    }
                }
            }

            final Message j = new Message(5);
            j.writer().writeLong(xpup);
            j.writer().flush();
            this.sendMessage(j);
            j.cleanup();
            if (oldLv != this.nj.get().getLevel()) {
                this.nj.setXPLoadSkill(this.nj.get().getExp());
                TaskHandle.requestLevel(nj);
            }

            this.nj.clan.clevel = this.nj.get().getLevel();
        }
    }

    public void upExpDown(long xpup) throws IOException {
        final Body value = this.nj.get();
        synchronized (this) {
            value.expdown -= xpup;
        }

        final Message m = new Message(71);
        m.writer().writeLong(xpup);
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    }

    public void setEffect(final int id, final int timeStart, final int timeLength, final int param) {
        try {
            final EffectData data = EffectData.entrys.get(id);
            Effect eff = this.nj.get().getEffType(data.type);
            if (eff == null) {
                eff = new Effect(id, timeStart, timeLength, param);
                synchronized (this.nj.get().getVeff()) {
                    this.nj.get().addEffect(eff);
                }
                this.addEffectMessage(eff);
            } else {
                eff.template = data;
                eff.timeLength = timeLength;
                eff.timeStart = timeStart;
                eff.param = param;
                eff.timeRemove = System.currentTimeMillis() - eff.timeStart + eff.timeLength;
                this.setEffectMessage(eff);
            }
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }


    public void addEffectMessage(final Effect eff) throws IOException {
        Message m = new Message(-30);

        try {


            m.writer().writeByte(-101);
            m.writer().writeByte(eff.template.id);
            m.writer().writeInt(eff.timeStart);
            m.writer().writeInt((int) (eff.timeRemove - System.currentTimeMillis()));
            m.writer().writeShort(eff.param);
            if (eff.template.type == 2 || eff.template.type == 3 || eff.template.type == 14) {
                m.writer().writeShort(this.nj.get().x);
                m.writer().writeShort(this.nj.get().y);
            }
            m.writer().flush();
            this.sendMessage(m);
            m.cleanup();


            m = new Message(-30);
            m.writer().writeByte(-98);
            m.writer().writeInt(this.nj.get().id);
            m.writer().writeByte(eff.template.id);
            m.writer().writeInt(eff.timeStart);
            m.writer().writeInt((int) (eff.timeRemove - System.currentTimeMillis()));
            m.writer().writeShort(eff.param);
            if (eff.template.type == 2 || eff.template.type == 3 || eff.template.type == 14) {
                m.writer().writeShort(this.nj.get().x);
                m.writer().writeShort(this.nj.get().y);
            }
            m.writer().flush();
            if (this.nj.getPlace() != null) {
                this.nj.getPlace().sendMessage(m);
            }
        } finally {
            m.cleanup();
        }
    }

    private void setEffectMessage(final Effect eff) throws IOException {
        Message m = new Message(-30);
        m.writer().writeByte(-100);
        m.writer().writeByte(eff.template.id);
        m.writer().writeInt(eff.timeStart);
        m.writer().writeInt(eff.timeLength);
        m.writer().writeShort(eff.param);
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
        m = new Message(-30);
        m.writer().writeByte(-97);
        m.writer().writeInt(this.nj.get().id);
        m.writer().writeByte(eff.template.id);
        m.writer().writeInt(eff.timeStart);
        m.writer().writeInt(eff.timeLength);
        m.writer().writeShort(eff.param);
        m.writer().flush();

        if (this.nj.getPlace() != null) {
            this.nj.getPlace().sendMessage(m);
        }
        m.cleanup();
    }

    public void removeEffect(final int id) {
        try {
            for (byte i = 0; i < this.nj.get().getVeff().size(); ++i) {
                final Effect eff = this.nj.get().getVeff().get(i);
                if (eff != null && eff.template.id == id) {
                    this.nj.get().remove(eff);
                    this.removeEffectMessage(eff);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void removeEffectMessage(final Effect eff) {
        try {
            Message m = new Message(-30);
            m.writer().writeByte(-99);
            m.writer().writeByte(eff.template.id);
            if (eff.template.type == 0 || eff.template.type == 12) {
                m.writer().writeInt(this.nj.get().hp);
                m.writer().writeInt(this.nj.get().mp);
            } else if (eff.template.type == 4 || eff.template.type == 13 || eff.template.type == 17) {
                m.writer().writeInt(this.nj.get().hp);
            } else if (eff.template.type == 23) {
                m.writer().writeInt(this.nj.get().hp);
                m.writer().writeInt(this.nj.get().getMaxHP());
            }
            m.writer().flush();
            this.sendMessage(m);
            m.writer().flush();
            m.cleanup();
            m = new Message(-30);
            m.writer().writeByte(-96);
            m.writer().writeInt(this.nj.get().id);
            m.writer().writeByte(eff.template.id);
            if (eff.template.type == 0 || eff.template.type == 12) {
                m.writer().writeInt(this.nj.get().hp);
                m.writer().writeInt(this.nj.get().mp);
            } else if (eff.template.type == 11) {
                m.writer().writeShort(this.nj.get().x);
                m.writer().writeShort(this.nj.get().y);
            } else if (eff.template.type == 4 || eff.template.type == 13 || eff.template.type == 17) {
                m.writer().writeInt(this.nj.get().hp);
            } else if (eff.template.type == 23) {
                m.writer().writeInt(this.nj.get().hp);
                m.writer().writeInt(this.nj.get().getMaxHP());
            }
            m.writer().flush();
            this.nj.getPlace().sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean updateSysMounts() {
        final Item item = this.nj.get().ItemMounts[4];
        if (item == null) {
            return false;
        }
        if (item.getUpgrade() < 99) {
            this.nj.p.sendYellowMessage("Thú cưới chưa đạt cấp độ tối đa");
            return false;
        }
        if (item.sys < 4) {
            if (20 / (item.sys + 1) > nextInt(100)) {
                final Item item2 = item;
                ++item2.sys;
                item.setUpgrade(0);
                for (byte i = 0; i < item.option.size(); ++i) {
                    final Option op = item.option.get(i);
                    if (op.id == 65) {
                        op.param = 0;
                    } else if (op.id != 66) {
                        for (byte j = 0; j < useItem.arrOp.length; ++j) {
                            if (useItem.arrOp[j] == op.id) {
                                final Option option = op;
                                option.param -= useItem.arrParam[j] * 8;
                                break;
                            }
                        }
                    }
                }
                try {
                    this.loadMounts();
                } catch (IOException ex) {
                }
                this.nj.p.sendYellowMessage("Nâng cấp thành công, thú cưới được tặng 1 sao");
            } else {
                this.nj.p.sendYellowMessage("Nâng cấp thất bại, hao phí 1 Chuyển tinh thạch");
            }
            return true;
        }
        this.nj.p.sendYellowMessage("Không thể nâng thêm sao");
        return false;
    }

    public boolean updateXpMounts(final int xpup, final byte type) {
        final Item item = this.nj.get().ItemMounts[4];
        if (item == null) {
            this.nj.p.sendYellowMessage("Bạn cần có thú cưới");
            return false;
        }
        if (item.isExpires) {
            return false;
        }
        if (type == 0 && item.id != 443 && item.id != 523 && item.id != 524) {
            this.nj.p.sendYellowMessage("Chỉ sử dụng cho thú cưới");
            return false;
        }
        if (type == 1 && item.id != 485 && item.id != 524) {
            this.nj.p.sendYellowMessage("Chỉ sử dụng cho xe máy");
            return false;
        }
        if (item.getUpgrade() < 99) {
            boolean isuplv = false;
            byte i = 0;
            while (i < item.option.size()) {
                final Option op = item.option.get(i);
                if (op.id == 65) {
                    final Option option = op;
                    option.param += xpup;
                    if (op.param >= 1000) {
                        isuplv = true;
                        op.param = 0;
                        break;
                    }
                    break;
                } else {
                    ++i;
                }
            }
            if (isuplv) {
                final Item item2 = item;
                item2.setUpgrade(item2.getUpgrade() + 1);
                final int lv = item.getUpgrade() + 1;
                if (lv == 10 || lv == 20 || lv == 30 || lv == 40 || lv == 50 || lv == 60 || lv == 70 || lv == 80 || lv == 90) {
                    for (byte j = 0; j < item.option.size(); ++j) {
                        final Option op2 = item.option.get(j);
                        if (op2.id != 65 && op2.id != 66) {
                            for (byte k = 0; k < useItem.arrOp.length; ++k) {
                                if (useItem.arrOp[k] == op2.id) {
                                    final Option option2 = op2;
                                    option2.param += useItem.arrParam[k];
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            try {
                this.loadMounts();
            } catch (IOException ex) {
            }
            return true;
        }
        this.nj.p.sendYellowMessage("Thú cưới đã đạt cấp tối đa");
        return false;
    }

    public void loadMounts() throws IOException {
        final Message m = new Message(-30);
        m.writer().writeByte(-54);
        m.writer().writeInt(this.nj.get().id);
        for (byte i = 0; i < this.nj.get().ItemMounts.length; ++i) {
            final Item item = this.nj.get().ItemMounts[i];
            if (item != null) {
                m.writer().writeShort(item.id);
                m.writer().writeByte(item.getUpgrade());
                m.writer().writeLong(item.expires);
                m.writer().writeByte(item.sys);
                m.writer().writeByte(item.option.size());
                for (byte j = 0; j < item.option.size(); ++j) {
                    m.writer().writeByte(item.option.get(j).id);
                    m.writer().writeInt(item.option.get(j).param);
                }
            } else {
                m.writer().writeShort(-1);
            }
        }
        m.writer().flush();
        this.nj.getPlace().sendMessage(m);
        m.cleanup();
    }

    public boolean dungThucan(final byte id, final int param, final int thoigian) {
        final Effect eff = this.nj.get().getEffType((byte) 0);
        if (this.nj.get().pk > 14) {
            this.sendYellowMessage("Điểm hiếu chiến quá cao không thể dùng được vật phẩm này");
            return false;
        }
        if (eff != null && eff.param > param) {
            this.sendYellowMessage("Đã có hiệu quả thức ăn cao hơn");
            return false;
        }
        this.setEffect(id, 0, 1000 * thoigian, param);
        return true;
    }

    public boolean buffHP(final int param) {
        final Effect eff = this.nj.get().getEffType((byte) 17);
        if (eff != null) {
            return false;
        }
        if (this.nj.get().pk > 14) {
            this.sendYellowMessage("Điểm hiếu chiến quá cao không thể dùng được vật phẩm này");
            return false;
        }
        if (this.nj.get().hp >= this.nj.get().getMaxHP()) {
            this.sendYellowMessage("HP đã đầy");
            return false;
        }
        this.setEffect(21, 0, 3000, param);
        return true;
    }

    public boolean buffMP(final int param) {
        if (this.nj.get().pk > 14) {
            this.sendYellowMessage("Điểm hiếu chiến quá cao không thể dùng được vật phẩm này");
            return false;
        }
        if (this.nj.get().mp >= this.nj.get().getMaxMP()) {
            this.sendYellowMessage("MP đã đầy");
            try {
                this.getMp();
            } catch (IOException ex) {
            }
            return false;
        }
        this.nj.get().upMP(param);
        try {
            this.getMp();
        } catch (IOException ex2) {
        }
        return true;
    }

    public void mobMeMessage(final int id, final byte boss) {
        try {
            if (id > 0) {
                final Mob mob = new Mob(-1, id, 0);
                mob.sys = 1;
                mob.status = 5;
                final Mob mob2 = mob;
                final Mob mob3 = mob;
                final int n = 0;
                mob3.hpmax = n;
                mob2.hp = n;
                mob.setIsboss((boss != 0));
                this.nj.get().mobMe = mob;
            } else {
                this.nj.get().mobMe = null;
            }
            Message m = new Message(-30);
            m.writer().writeByte(-69);
            m.writer().writeByte(id);
            m.writer().writeByte(boss);
            m.writer().flush();
            this.sendMessage(m);
            m.cleanup();
            if (this.nj.getPlace() == null) {
                return;
            }
            m = new Message(-30);
            m.writer().writeByte(-68);
            m.writer().writeInt(this.nj.get().id);
            m.writer().writeByte(id);
            m.writer().writeByte(boss);
            m.writer().flush();
            this.nj.getPlace().sendMyMessage(this, m);
            m.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTimeMap(final int timeLength) {
        try {
            final Message m = new Message(-30);
            m.writer().writeByte(-95);
            m.writer().writeInt(timeLength);
            m.writer().flush();
            this.sendMessage(m);
            m.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPointPB(final int point) {
        try {
            final Message m = new Message(-28);
            m.writer().writeByte(-84);
            m.writer().writeShort(point);
            m.writer().flush();
            this.sendMessage(m);
            m.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void restCave() {
        try {
            final Message m = new Message(-16);
            m.writer().flush();
            this.sendMessage(m);
            m.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void rewardedCave() {
        final int num = this.nj.pointCave / 10;
        if (num > 0) {
            if (this.nj.getAvailableBag() == 0) {
                this.session.sendMessageLog("Hành trang không đủ chỗ trống");
                return;
            }
            Item item;
            if (this.nj.getLevel() < 50) {
                item = new Item();
                item.id = 272;
            } else if (this.nj.getLevel() < 90) {
                item = new Item();
                item.id = 282;
            } else {
                item = new Item();
                item.id = 647;
            }
            item.quantity = num;
            this.nj.addItemBag(true, item);
            this.nj.pointCave = 0;
            if (this.nj.bagCaveMax < num) {
                this.nj.bagCaveMax = num;
                this.nj.itemIDCaveMax = item.id;
            }
        }
    }

    public void chatParty(Message m) throws IOException {
        final String text = m.reader().readUTF();
        m.cleanup();
        if (this.nj.get().party != null) {
            m = new Message(-20);
            m.writer().writeUTF(this.nj.name);
            m.writer().writeUTF(text);
            m.writer().flush();
            for (byte i = 0; i < this.nj.get().party.ninjas.size(); ++i) {
                this.nj.get().party.ninjas.get(i).p.sendMessage(m);
            }
            m.cleanup();
        }
    }

    public void addParty(final Message m) throws IOException {
        final String name = m.reader().readUTF();
        m.cleanup();
        final Ninja n = PlayerManager.getInstance().getNinja(name);
        if (n != null) {
            if (n.get().party != null) {
                this.sendYellowMessage("Đối phương đã có nhóm");
            } else if (this.nj.get().party != null) {
                if (this.nj.get().party.master != this.nj.id) {
                    this.sendYellowMessage("Bạn không phải nhóm trưởng");
                } else {
                    this.nj.get().party.addParty(this, n.p);
                }
            } else {
                (this.nj.get().party = new Party(this.nj)).addPartyAccept(this.nj);
                this.nj.get().party.addParty(this, n.p);
            }
        }
    }

    public void addPartyAccept(final Message m) throws IOException {
        final int charId = m.reader().readInt();
        m.cleanup();
        if (this.nj.party != null) {
            return;
        }
        final Ninja n = PlayerManager.getInstance().getNinja(charId);
        if (n != null && n.party != null) {
            final Party party = n.party;
            if (party.ninjas.size() > 5) {
                this.sendYellowMessage("Nhóm đã đủ thành viên");
                return;
            }
            for (short i = 0; i < party.pt.size(); ++i) {
                if (party.pt.get(i) == this.session.id) {
                    party.pt.remove(i);
                    party.addPartyAccept(this.nj);
                    party.refreshTeam();
                    return;
                }
            }
        } else {
            this.sendYellowMessage("Nhóm này đã không tồn tại");
        }
    }

    public void moveMemberParty(final Message m) throws IOException {
        final byte index = m.reader().readByte();
        m.cleanup();
        if (this.nj.get().party != null && this.nj.get().id == this.nj.get().party.master && index >= 0 && index < this.nj.get().party.ninjas.size()) {
            final Ninja n = this.nj.get().party.ninjas.get(index);
            if (n.id != this.nj.id) {
                this.nj.get().party.moveMember(index);
            }
        }
    }

    public void changeTeamLeaderParty(final Message m) throws IOException {
        final byte index = m.reader().readByte();
        m.cleanup();
        if (this.nj.get().party != null && this.nj.id == this.nj.get().party.master && index >= 0 && index < this.nj.get().party.ninjas.size()) {
            this.nj.get().party.changeTeamLeader(index);
        }
    }

    private void createParty() {
        if (this.nj.get().party == null) {
            final Party party = new Party(this.nj);
            party.addPartyAccept(this.nj);
        }
    }

    public void getMobMe() {
        if (this.nj.get().ItemBody[10] != null) {
            switch (this.nj.get().ItemBody[10].id) {
                case 246: {
                    this.mobMeMessage(70, (byte) 0);
                    break;
                }
                case 419: {
                    this.mobMeMessage(122, (byte) 0);
                    break;
                }
                case 568: {
                    this.mobMeMessage(205, (byte) 0);
                    break;
                }
                case 569: {
                    this.mobMeMessage(206, (byte) 0);
                    break;
                }
                case 570: {
                    this.mobMeMessage(207, (byte) 0);
                    break;
                }
                case 571: {
                    this.mobMeMessage(208, (byte) 0);
                    break;
                }
                case 583: {
                    this.mobMeMessage(211, (byte) 1);
                    break;
                }
                case 584: {
                    this.mobMeMessage(212, (byte) 1);
                    break;
                }
                case 585: {
                    this.mobMeMessage(213, (byte) 1);
                    break;
                }
                case 586: {
                    this.mobMeMessage(214, (byte) 1);
                    break;
                }
                case 587: {
                    this.mobMeMessage(215, (byte) 1);
                    break;
                }
                case 588: {
                    this.mobMeMessage(216, (byte) 1);
                    break;
                }
                case 589: {
                    this.mobMeMessage(217, (byte) 1);
                    break;
                }
                case 742: {
                    this.mobMeMessage(229, (byte) 1);
                    break;
                }
                case 772: {
                    this.mobMeMessage(234, (byte) 1);
                    break;
                }
                case 773: {
                    this.mobMeMessage(234, (byte) 1);
                    break;
                }
                case 838: {
                    this.mobMeMessage(220, (byte) 1);
                    break;
                }
                case 837: {
                    this.mobMeMessage(223, (byte) 1);
                    break;
                }
                case 836: {
                    this.mobMeMessage(224, (byte) 1);
                    break;
                }
                case 846: {
                    this.mobMeMessage(221, (byte) 1);
                    break;
                }
                case 781: {
                    this.mobMeMessage(235, (byte) 1);
                    break;
                }
                default: {
                    this.mobMeMessage(0, (byte) 0);
                    break;
                }
            }
        } else {
            this.mobMeMessage(0, (byte) 0);
        }
    }

    public void toNhanBan() {
        if (!this.nj.isNhanban) {
            if (this.nj.party != null) {
                this.nj.party.exitParty(this.nj);
            }
            for (byte n = 0; n < this.nj.get().getVeff().size(); ++n) {
                this.removeEffectMessage(this.nj.get().getVeff().get(n));
            }
            this.nj.isNhanban = true;
            this.nj.isHuman = false;
            this.nj.clone.setIslive(true);
            this.nj.clone.x = this.nj.x;
            this.nj.clone.y = this.nj.y;
            this.nj.getPlace().removeMessage(this.nj.clone.id);
            this.nj.getPlace().removeMessage(this.nj.id);
            Service.CharViewInfo(this);
            GameScr.sendSkill(this, "KSkill");
            GameScr.sendSkill(this, "OSkill");
            GameScr.sendSkill(this, "CSkill");
            for (User user : this.nj.getPlace().getUsers()) {
                if (user.id != this.id) {
                    this.nj.getPlace().sendCharInfo(this, user);
                    this.nj.getPlace().sendCoat(this.nj.get(), user);
                    this.nj.getPlace().sendGlove(this.nj.get(), user);
                }
                this.nj.getPlace().sendMounts(this.nj.get(), user);
            }
        }
    }

    public void exitNhanBan(final boolean isAlive) {
        if (this.nj.isNhanban) {
            if (this.nj.clone.party != null) {
                this.nj.clone.party.exitParty(this.nj);
            }
            for (byte n = 0; n < this.nj.get().getVeff().size(); ++n) {
                this.removeEffectMessage(this.nj.get().getVeff().get(n));
            }
            this.nj.isNhanban = false;
            this.nj.isHuman = true;
            this.nj.clone.setIslive(isAlive);
            this.nj.x = this.nj.clone.x;
            this.nj.y = this.nj.clone.y;
            this.nj.clone.refresh();
            this.nj.getPlace().removeMessage(this.nj.clone.id);
            Service.CharViewInfo(this);
            GameScr.sendSkill(this, "KSkill");
            GameScr.sendSkill(this, "OSkill");
            GameScr.sendSkill(this, "CSkill");
            for (User user : this.nj.getPlace().getUsers()) {

                if (user.id != this.id) {
                    this.nj.getPlace().sendCharInfo(this, user);
                    this.nj.getPlace().sendCoat(this.nj.get(), user);
                    this.nj.getPlace().sendGlove(this.nj.get(), user);
                }
                this.nj.getPlace().sendMounts(this.nj.get(), user);
            }
            if (!isAlive) {
                for (User user : this.nj.getPlace().getUsers()) {
                    Service.sendclonechar(this.nj.p, user);
                }
            }
        }
    }


    public void setNj(Ninja nj) {
        this.nj = nj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(nj, user.nj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nj);
    }

    @Nullable
    public ClanTerritoryData getClanTerritoryData() {
        if (clanTerritoryData == null) {
            this.clanTerritoryData = Server.clanTerritoryManager.getClanTerritoryDataById(this.clanTerritoryId);
        }
        return clanTerritoryData;
    }

    public void setClanTerritoryData(ClanTerritoryData clanTerritoryData) {
        this.clanTerritoryData = clanTerritoryData;
        if (clanTerritoryData != null) {
            this.setClanTerritoryId(clanTerritoryData.id);
        } else {
            this.setClanTerritoryId(-1);
        }
    }

    public int getClanTerritoryId() {
        return clanTerritoryData == null ? -1 : clanTerritoryData.id;
    }

    public void setClanTerritoryId(int clanTerritoryId) {

        if (clanTerritoryId == -1) {
            this.clanTerritoryData = null;
            this.clanTerritoryId = -1;
        } else {
            ClanTerritoryData clanData = Server.clanTerritoryManager.getClanTerritoryDataById(clanTerritoryId);
            if (clanData == null) {
                this.clanTerritoryId = -1;
            } else {
                this.clanTerritoryId = clanTerritoryId;
                this.clanTerritoryData = clanData;
            }
        }
    }

    public void acceptInviteGT(int ninjaId) {
        val rivalNinja = PlayerManager.getInstance().getNinja(ninjaId);
        if (rivalNinja != null) {
            val yourNinja = nj;
            val area = Server.getMapById(110).getFreeArea();
            if (area == null) {


                sendYellowMessage(TẤT_CẢ_CÁC_KHU_ĐẶT_CƯỢC_ĐỀU_FULL);
                rivalNinja.p.sendYellowMessage(TẤT_CẢ_CÁC_KHU_ĐẶT_CƯỢC_ĐỀU_FULL);
                return;
            }
            val clanBattle = new ClanBattle(yourNinja, rivalNinja);

            yourNinja.setClanBattle(clanBattle);
            rivalNinja.setClanBattle(clanBattle);

            rivalNinja.enterSamePlace(area, yourNinja);
        } else {
            sendYellowMessage("Đối thủ đã offline");
        }
    }

    public int countMat() {
        int count = 0;
        for (Item item : this.nj.ItemBag) {
            if (item != null && item.getData().isEye()) {
                count++;
            }
        }

        for (Item itemBox : this.nj.ItemBox) {
            if (itemBox != null && itemBox.getData().isEye()) {
                count++;
            }
        }

        for (Item item : this.nj.ItemBody) {
            if (item != null && item.getData().isEye()) {
                count++;
            }
        }

        if (nj.clone != null) {
            for (Item item : this.nj.clone.ItemBody) {
                if (item != null && item.getData().isEye()) {
                    count++;
                }
            }
        }

        return count;
    }

    @Override
    public void sendMessage(Message message) {
        if (session != null) {
            session.sendMessage(message);
        }
    }

    public void addCuuSat(Message m) throws IOException {
        final int pid = m.reader().readInt();
        m.cleanup();
        if (GameScr.mapNotPK(this.nj.getMapId())) {
            return;
        }
        for (int i = 0; i < nj.place.getUsers().size(); i++) {
            User player = nj.place.getUsers().get(i);
            if (player.nj.id == pid) {
                m = new Message(68);
                m.writer().writeInt(this.nj.id);
                player.session.sendMessage(m);
                m.cleanup();
                player.nj.isCuuSat = true;
            }
        }
        m = new Message(69);
        m.writer().writeInt(pid);
        this.session.sendMessage(m);
        m.cleanup();
        this.nj.addCuuSat = true;
    }

    public void removeCuuSat(User c) throws IOException {
        Message m = new Message(70);
        m.writer().writeInt(c.id);
        this.session.sendMessage(m);
        m.cleanup();
        m = new Message(70);
        this.session.sendMessage(m);
        m.cleanup();
        this.nj.addCuuSat = false;
        this.nj.isCuuSat = false;
    }
    public void sendItem() {
        try {

            Ninja userGF = PlayerManager.getInstance().getNinja(nameUS);
            short idItemGFF = Short.parseShort(idItemGF);
            int itemQuantityGFF = Integer.parseInt(itemQuantityGF);
            Item itemGF = new Item();


            for (byte i = 0; i < itemQuantityGFF; i++) {
                itemGF = ItemData.itemDefault(idItemGFF);
                userGF.addItemBag(true, itemGF);
            }
        } catch (Exception e) {
            this.sendYellowMessage("Vật phẩm này không tồn tại!");
        }

    }

    public void sendLuong() {
        try {
            Ninja userGF = PlayerManager.getInstance().getNinja(nameUS);
            int luongGFF = Integer.parseInt(luongGF);
            userGF.p.upluongMessage(luongGFF);
        } catch (Exception e) {
            this.sendYellowMessage("Hãy nhập đúng định dạng");
        }

    }

    public void sendXu() {
        try {
            Ninja userGF = PlayerManager.getInstance().getNinja(nameUS);
            int xuGFF = Integer.parseInt(xuGF);
            userGF.upxuMessage(xuGFF);
        } catch (Exception e) {
            this.sendYellowMessage("Hãy nhập đúng định dạng");
        }

    }

    public void sendYen() {
        try {
            Ninja userGF = PlayerManager.getInstance().getNinja(nameUS);
            int yenGFF = Integer.parseInt(yenGF);
            userGF.upyenMessage(yenGFF);
        } catch (Exception e) {
            this.sendYellowMessage("Hãy nhập đúng định dạng");

        }

    }
    public void Kinhmach() {
        if (nj.lvkm ==0) {
            session.sendMessageLog("Mày chưa có kinh mạch nên kém vl"                
            );
        }
        else if (nj.lvkm ==1) {
            session.sendMessageLog("Bạn đang tu luyện kinh mạch đến tầng 1"
                + "\n Hiệu ứng đang nhận được là: "
                + "\n +5.000 dame lên quái"
                + "\n 1% tỷ lệ xuất hiện hút máu từ quái - tương đương 10% dame"                
            );
        }else if (nj.lvkm ==2) {
            this.sendYellowMessage("Bạn đang tu luyện kinh mạch đến tầng 2"
                + "\n Hiệu ứng đang nhận được là: "
                + "\n +7.000 dame lên quái"
                + "\n 2% tỷ lệ xuất hiện hút máu từ quái - tương đương 10% dame"                
            );
        }
        else if (nj.lvkm ==3) {
            session.sendMessageLog("Bạn đang tu luyện kinh mạch đến tầng 3"
                + "\n Hiệu ứng đang nhận được là: "
                + "\n +9.000 dame lên quái"
                + "\n 3% tỷ lệ xuất hiện hút máu từ quái - tương đương 10% dame"                
            );
        }
        else if (nj.lvkm ==4) {
            session.sendMessageLog("Bạn đang tu luyện kinh mạch đến tầng 4"
                + "\n Hiệu ứng đang nhận được là: "
                + "\n +11.000 dame lên quái"
                + "\n 4% tỷ lệ xuất hiện hút máu từ quái - tương đương 10% dame"                
            );
        }
        else if (nj.lvkm ==5) {
            session.sendMessageLog("Bạn đang tu luyện kinh mạch đến tầng 5"
                + "\n Hiệu ứng đang nhận được là: "
                + "\n +13.000 dame lên quái"
                + "\n 5% tỷ lệ xuất hiện hút máu từ quái - tương đương 10% dame"                
            );
        }
        else if (nj.lvkm ==6) {
            session.sendMessageLog("Bạn đang tu luyện kinh mạch đến tầng 6"
                + "\n Hiệu ứng đang nhận được là: "
                + "\n +15.000 dame lên quái"
                + "\n 6% tỷ lệ xuất hiện hút máu từ quái - tương đương 10% dame"                
            );
        }
        else if (nj.lvkm ==7) {
            session.sendMessageLog("Bạn đang tu luyện kinh mạch đến tầng 7"
                + "\n Hiệu ứng đang nhận được là: "
                + "\n +18.000 dame lên quái"
                + "\n 7% tỷ lệ xuất hiện hút máu từ quái - tương đương 10% dame"                
            );
        }
        else if (nj.lvkm ==8) {
            session.sendMessageLog("Bạn đang tu luyện kinh mạch đến tầng 8"
                + "\n Hiệu ứng đang nhận được là: "
                + "\n +21.000 dame lên quái"
                + "\n 8% tỷ lệ xuất hiện hút máu từ quái - tương đương 10% dame"                
            );
        }
        else if (nj.lvkm ==9) {
            session.sendMessageLog("Bạn đang tu luyện kinh mạch đến tầng cuối cùng"
                + "\n Hiệu ứng đang nhận được là: "
                + "\n +25.000 dame lên quái"
                + "\n 10% tỷ lệ xuất hiện hút máu từ quái - tương đương 10% dame"                
            );
        }
    }

    public void sendMess() {
        Ninja userGF = PlayerManager.getInstance().getNinja(nameUS);
        userGF.p.session.sendMessageLog(messGF);
    }
    public void sendItem1() {
        try {
            Ninja UserGF = PlayerManager.getInstance().getNinja(nameUS);
            int idItemGFF = Integer.parseInt(idItemGF);
            int itemQuantityGFF = Integer.parseInt(itemQuantityGF);
            Item itemGF = new Item();
            if (itemQuantityGFF <120 ) {
                for (byte i = 0; i < itemQuantityGFF; i++) {
                itemGF = ItemData.itemDefault(idItemGFF);
                UserGF.addItemBag(true, itemGF);
                }
            } else {
                itemGF = ItemData.itemDefault(idItemGFF);
                itemGF.quantity = itemQuantityGFF;
                UserGF.addItemBag(true, itemGF);
            }
        } catch (Exception e) {
            sendYellowMessage("Vật phẩm này không tồn tại!");
        }

    }
    
    public void sendTB() {
        try {
            Ninja UserGF = PlayerManager.getInstance().getNinja(nameUS);
            int idItemGFF = Integer.parseInt(idItemGF);
            int itemQuantityGFF = Integer.parseInt(itemQuantityGF);
            int itemUpgradeGFF = Integer.parseInt(itemUpgradeGF);
            byte itemSysGFF = Byte.parseByte(itemSysGF);
            for (byte i = 0; i < itemQuantityGFF; i++) {
                final int idI = idItemGFF ;
                final ItemData data2 = ItemData.ItemDataId(idI);
                    Item itemup;
                    if (data2.type < 10) {
                        if (data2.type == 1) {
                            itemup = ItemData.itemDefault(idI);
                            itemup.sys = GameScr.SysClass(data2.nclass);
                        } else {
                            final byte sys = (byte) itemSysGFF;
                            itemup = ItemData.itemDefault(idI, sys);
                        }
                    } else {
                        itemup = ItemData.itemDefault(idI);
                    }
                    itemup.setLock(itemup.isLock());
                    for (final Option Option : itemup.option) {
                        final int idOp2 = Option.id;
                        Option.param = Option.param;
                    }
                    itemup.upgradeNext((byte) itemUpgradeGFF);
                    nj.addItemBag(true, itemup);
            }
        } catch (Exception e) {
            sendYellowMessage("Vật phẩm này không tồn tại!");
        }

    }
}
