package server;

import lombok.SneakyThrows;
import lombok.val;
import patch.interfaces.IBattle;
import patch.interfaces.SendMessage;
import patch.tournament.TournamentData;
import real.*;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import real.User;
import tasks.TaskList;
import tasks.TaskTemplate;
import threading.Message;

public class Service {
    protected static Message messageSubCommand(final byte command) throws Exception {
        final Message message = new Message(-30);
        message.writer().writeByte(command);
        return message;

    }

    public static void openUIMenu(Ninja _ninja, String[] menu) {
        Message msg = null;
        try {
            msg = new Message((byte) 40);
            if (menu != null) {
                for (byte i = 0; i < menu.length; i = (byte) (i + 1)) {
                    if (menu[i] != null) {
                        msg.writer().writeUTF(menu[i]);
                    }
                }
            }
            _ninja.p.session.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (msg != null)
                msg.cleanup();
        }
    }


    public static void openUIConfirm(Ninja _char, short npcTemplateId, String chat, String[] menu) {
        Message msg = null;
        try {
            msg = new Message((byte) 39);
            msg.writer().writeShort(npcTemplateId);
            msg.writer().writeUTF(chat);
            msg.writer().writeByte(menu.length);
            byte i;
            for (i = 0; i < menu.length; i = (byte) (i + 1)) {
                msg.writer().writeUTF(menu[i]);
            }
            _char.p.session.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (msg != null)
                msg.cleanup();
        }
    }

    protected static Message messageNotLogin(final byte command) throws Exception {
        final Message message = new Message(-29);
        message.writer().writeByte(command);
        return message;
    }

    public static Message messageNotMap(final byte command) throws Exception {
        final Message message = new Message(-28);
        message.writer().writeByte(command);
        return message;
    }

    public static void evaluateCave(final Ninja nj) {
        Message msg = null;
        try {
            msg = messageNotMap((byte) (-83));
            msg.writer().writeShort(nj.pointCave);
            msg.writer().writeShort(2);
            msg.writer().writeByte(0);
            msg.writer().writeShort(nj.pointCave / 10);
            nj.p.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public static Message messageSubCommand2(int command) {
        Message message = new Message(-30);

        try {
            message.writer().writeByte(command - 128);
            return message;
        } catch (Exception e) {
            return message;
        }
    }


    @SneakyThrows
    public static void batDauTinhGio(SendMessage sendMes, int second) {
        if (sendMes == null) return;
        val message = messageSubCommand2(33);
        message.writer().writeInt(second);
        message.writer().flush();
        sendMes.sendMessage(message);
        message.cleanup();

    }


    public static void sendclonechar(final User p, final User top) {
        try {
            Message m = new Message(3);
            m.writer().writeInt(p.nj.clone.id);
            m.writer().writeUTF("");
            m.writer().writeBoolean(false);
            m.writer().writeByte(p.nj.clone.getTypepk());
            m.writer().writeByte(p.nj.clone.nclass);
            m.writer().writeByte(p.nj.clone.chuThan.gender);
            m.writer().writeShort(p.nj.clone.partHead());
            m.writer().writeUTF(p.nj.clone.chuThan.name);
            m.writer().writeInt(p.nj.clone.hp);
            m.writer().writeInt(p.nj.clone.getMaxHP());
            m.writer().writeByte(p.nj.clone.getLevel());
            m.writer().writeShort(p.nj.clone.Weapon());
            m.writer().writeShort(p.nj.clone.partBody());
            m.writer().writeShort(p.nj.clone.partLeg());
            m.writer().writeByte(-1);
            m.writer().writeShort(p.nj.clone.x);
            m.writer().writeShort(p.nj.clone.y);
            m.writer().writeShort(p.nj.eff5buffHP());
            m.writer().writeShort(p.nj.eff5buffMP());
            m.writer().writeByte(0);
            m.writer().writeBoolean(p.nj.clone.isHuman);
            m.writer().writeBoolean(p.nj.clone.isNhanban);
            m.writer().writeShort(-1);
            m.writer().writeShort(-1);
            m.writer().writeShort(-1);
            m.writer().writeShort(-1);
            Item item0 = p.nj.clone.ItemBody[18];//Đầu thân chân
            if (item0 != null) {
                if (item0.id == 795) {//Thiên Nguyệt Chi Nữ
                    m.writer().writeShort(37);
                    m.writer().writeShort(38);
                    m.writer().writeShort(39);
                } else if (item0.id == 796) {//Nhật Tử Lam Phong
                    m.writer().writeShort(40);
                    m.writer().writeShort(41);
                    m.writer().writeShort(42);
                } else if (item0.id == 804) {//Hajiro
                    m.writer().writeShort(58);
                    m.writer().writeShort(59);
                    m.writer().writeShort(60);
                } else if (item0.id == 805) {//Shiraiji
                    m.writer().writeShort(55);
                    m.writer().writeShort(56);
                    m.writer().writeShort(57);
                } else if (item0.id == 850) {//Mặt nạ hổ
                    m.writer().writeShort(69-p.nj.gender*3);
                    m.writer().writeShort(70-p.nj.gender*3);
                    m.writer().writeShort(71-p.nj.gender*3);
                } else {
                    m.writer().writeShort(-1);
                    m.writer().writeShort(-1);
                    m.writer().writeShort(-1);
                }
            } else {
                m.writer().writeShort(-1);
                m.writer().writeShort(-1);
                m.writer().writeShort(-1);
            }
            Item item3 = p.nj.clone.ItemBody[17];//Vũ khí
            if (item3 != null) {
                if (item3.id == 799) {//Gậy Mặt Trăng
                    m.writer().writeShort(44);
                } else if (item3.id == 800) {//Gậy Trái tim
                    m.writer().writeShort(46);
                } else {
                    m.writer().writeShort(-1);
                }
            } else {
                m.writer().writeShort(-1);
            }
            Item item4 = p.nj.clone.ItemBody[12];//Yoroi
            if (item4 != null) {
                if (item4.id == 797) {//Hakairo Yoroi
                    m.writer().writeShort(43);
                } else {
                    m.writer().writeShort(-1);
                }
            } else {
                m.writer().writeShort(-1);
            }
            m.writer().writeShort(-1);//name
            Item item6 = p.nj.clone.ItemMounts[4];//Thú cưỡi
            if (item6 != null) {
                if (item6.id == 798) {//Lân Sư Vũ
                    m.writer().writeShort(36);
                } else if (item6.id == 801) {//Xích Tử Mã
                    m.writer().writeShort(47);
                } else if (item6.id == 802) {//Tà Linh Mã
                    m.writer().writeShort(48);
                } else if (item6.id == 803) {//Phong Thương Mã
                    m.writer().writeShort(49);
                } else if (item6.id == 839) {//Phượng Hoàng Băng
                    m.writer().writeShort(63);
                } else if (item6.id == 851) {
                    m.writer().writeShort(72);
                } else {
                    m.writer().writeShort(-1);
                }
            } else {
                m.writer().writeShort(-1);
            }
            m.writer().writeShort(-1);//rank
            Item item8 = p.nj.clone.ItemBody[27];//mặt nạ
            if (item8 != null) {
                if (item8.id == 813) {//Mặt nạ Shin Ah
                    m.writer().writeShort(54);
                } else if (item8.id == 814) {//Mặt nạ Vô Diện
                    m.writer().writeShort(53);
                } else if (item8.id == 815) {//Mặt nạ Oni
                    m.writer().writeShort(52);
                } else if (item8.id == 816) {//Mặt nạ Kuma
                    m.writer().writeShort(51);
                } else if (item8.id == 817) {//Mặt nạ Inu
                    m.writer().writeShort(50);
                } else {
                    m.writer().writeShort(-1);
                }
            } else {
                m.writer().writeShort(-1);
            }
            Item item9 = p.nj.clone.ItemBody[26];//bienhinh
            if (item9 != null) {
                if (item9.id == 825) {//Pet Bóng Ma
                    m.writer().writeShort(61);
                } else if (item9.id == 826) {//Pet Yêu Tinh
                    m.writer().writeShort(62);
                } else if (item9.id == 852) {
                    m.writer().writeShort(74);
                } else {
                    m.writer().writeShort(-1);
                }
            } else {
                m.writer().writeShort(-1);
            }
            for (int k = 16; k < 32; ++k) {//Trang bị 2
                final Item item = p.nj.clone.ItemBody[k];
                if (item != null) {
                    m.writer().writeShort(item.id);
                    m.writer().writeByte(item.getUpgrade());
                    m.writer().writeByte(item.sys);
                }
                else {
                    m.writer().writeShort(-1);
                }
            }
            m.writer().flush();
            top.sendMessage(m);
            m.cleanup();
            if (p.nj.clone.mobMe != null) {
                m = new Message(-30);
                m.writer().writeByte(-68);
                m.writer().writeInt(p.nj.clone.id);
                m.writer().writeByte(p.nj.clone.mobMe.templates.id);
                m.writer().writeByte(p.nj.clone.mobMe.isIsboss() ? 1 : 0);
                m.writer().flush();
                top.sendMessage(m);
                m.cleanup();
            }
            p.nj.getPlace().sendCoat(p.nj.clone, top);
            p.nj.getPlace().sendGlove(p.nj.clone, top);
            p.nj.getPlace().sendMounts(p.nj.clone, top);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setHPMob(final Ninja nj, final int mobid, final int hp) {
        Message msg = null;
        try {
            msg = new Message(51);
            msg.writer().writeByte(mobid);
            msg.writer().writeInt(0);
            nj.p.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public static void CharViewInfo(final User p) {
        CharViewInfo(p, true);
    }

    public static void CharViewInfo(final User p, boolean sendEff) {
        Message msg = null;
        try {
            final Ninja c = p.nj;
            msg = messageSubCommand((byte) 115);
            msg.writer().writeInt(c.get().id);
            msg.writer().writeUTF(c.clan.clanName);
            if (!c.clan.clanName.isEmpty()) {
                msg.writer().writeByte(c.clan.typeclan);
            }
            msg.writer().writeByte(c.getTaskId());
            msg.writer().writeByte(c.gender);
            msg.writer().writeShort(c.get().partHead());
            msg.writer().writeByte(c.get().speed());
            msg.writer().writeUTF(c.name);
            msg.writer().writeByte(c.get().pk);
            msg.writer().writeByte(c.get().getTypepk());
            msg.writer().writeInt(c.get().getMaxHP());
            msg.writer().writeInt(c.get().hp);
            msg.writer().writeInt(c.get().getMaxMP());
            msg.writer().writeInt(c.get().mp);
            msg.writer().writeLong(c.get().getExp());
            msg.writer().writeLong(c.get().expdown);
            msg.writer().writeShort(c.get().eff5buffHP());
            msg.writer().writeShort(c.get().eff5buffMP());
            msg.writer().writeByte(c.get().nclass);
            msg.writer().writeShort(c.get().getPpoint());
            msg.writer().writeShort(c.get().getPotential0());
            msg.writer().writeShort(c.get().getPotential1());
            msg.writer().writeInt(c.get().getPotential2());
            msg.writer().writeInt(c.get().getPotential3());
            msg.writer().writeShort(c.get().getSpoint());
            msg.writer().writeByte(c.get().getSkills().size());
            for (short i = 0; i < c.get().getSkills().size(); ++i) {
                final Skill skill = c.get().getSkills().get(i);
                msg.writer().writeShort(SkillData.Templates(skill.id, skill.point).skillId);
            }
            msg.writer().writeInt(c.xu);
            msg.writer().writeInt(c.yen);
            msg.writer().writeInt(p.luong);
            msg.writer().writeByte(c.maxluggage);
            for (int j = 0; j < c.maxluggage; ++j) {
                final Item item = c.ItemBag[j];
                if (item != null) {
                    msg.writer().writeShort(item.id);
                    msg.writer().writeBoolean(item.isLock());
                    if (ItemData.isTypeBody(item.id) || ItemData.isTypeMounts(item.id) || ItemData.isTypeNgocKham(item.id)) {
                        msg.writer().writeByte(item.getUpgrade());
                    }
                    msg.writer().writeBoolean(item.isExpires);
                    msg.writer().writeShort(item.quantity);
                } else {
                    msg.writer().writeShort(-1);
                }
            }
            for (int k = 0; k < 16; ++k) {
                final Item item = c.get().ItemBody[k];
                if (item != null) {
                    msg.writer().writeShort(item.id);
                    msg.writer().writeByte(item.getUpgrade());
                    msg.writer().writeByte(item.sys);
                } else {
                    msg.writer().writeShort(-1);
                }
            }
            msg.writer().writeBoolean(c.isHuman);
            msg.writer().writeBoolean(c.isNhanban);
            msg.writer().writeShort(c.get().partHead());
            msg.writer().writeShort(c.get().Weapon());
            msg.writer().writeShort(c.get().partBody());
            msg.writer().writeShort(c.get().partLeg());
            Item item0 = c.get().ItemBody[18];//Đầu thân chân
            if (item0 != null) {
                if (item0.id == 795) {//Thiên Nguyệt Chi Nữ
                    msg.writer().writeShort(37);
                    msg.writer().writeShort(38);
                    msg.writer().writeShort(39);
                } else if (item0.id == 796) {//Nhật Tử Lam Phong
                    msg.writer().writeShort(40);
                    msg.writer().writeShort(41);
                    msg.writer().writeShort(42);
                } else if (item0.id == 804) {//Hajiro
                    msg.writer().writeShort(58);
                    msg.writer().writeShort(59);
                    msg.writer().writeShort(60);
                } else if (item0.id == 805) {//Shiraiji
                    msg.writer().writeShort(55);
                    msg.writer().writeShort(56);
                    msg.writer().writeShort(57);
                } else if (item0.id == 850) {//Mặt nạ hổ
                    msg.writer().writeShort(69-p.nj.gender*3);
                    msg.writer().writeShort(70-p.nj.gender*3);
                    msg.writer().writeShort(71-p.nj.gender*3);
                } else {
                    msg.writer().writeShort(-1);
                    msg.writer().writeShort(-1);
                    msg.writer().writeShort(-1);
                }
            } else {
                msg.writer().writeShort(-1);
                msg.writer().writeShort(-1);
                msg.writer().writeShort(-1);
            }
            Item item3 = c.get().ItemBody[17];//Vũ khí
            if (item3 != null) {
                if (item3.id == 799) {//Gậy Mặt Trăng
                    msg.writer().writeShort(44);
                } else if (item3.id == 800) {//Gậy Trái tim
                    msg.writer().writeShort(46);
                } else {
                    msg.writer().writeShort(-1);
                }
            } else {
                msg.writer().writeShort(-1);
            }
            Item item4 = c.get().ItemBody[12];//Yoroi
            if (item4 != null) {
                if (item4.id == 797) {//Hakairo Yoroi
                    msg.writer().writeShort(43);
                } else {
                    msg.writer().writeShort(-1);
                }
            } else {
                msg.writer().writeShort(-1);
            }
            msg.writer().writeShort(-1);//name
            Item item6 = c.get().ItemMounts[4];//Thú cưỡi
            if (item6 != null) {
                if (item6.id == 798) {//Lân Sư Vũ
                    msg.writer().writeShort(36);
                } else if (item6.id == 801) {//Xích Tử Mã
                    msg.writer().writeShort(47);
                } else if (item6.id == 802) {//Tà Linh Mã
                    msg.writer().writeShort(48);
                } else if (item6.id == 803) {//Phong Thương Mã
                    msg.writer().writeShort(49);
                } else if (item6.id == 839) {//Phu?ng Hoàng Bang
                    msg.writer().writeShort(63);
                } else if (item6.id == 851) {
                    msg.writer().writeShort(72);
                } else {
                    msg.writer().writeShort(-1);
                }
            } else {
                msg.writer().writeShort(-1);
            }
            msg.writer().writeShort(-1);//rank
            Item item8 = c.get().ItemBody[27];//mặt nạ
            if (item8 != null) {
                if (item8.id == 813) {//Mặt nạ Shin Ah
                    msg.writer().writeShort(54);
                } else if (item8.id == 814) {//Mặt nạ Vô Diện
                    msg.writer().writeShort(53);
                } else if (item8.id == 815) {//Mặt nạ Oni
                    msg.writer().writeShort(52);
                } else if (item8.id == 816) {//Mặt nạ Kuma
                    msg.writer().writeShort(51);
                } else if (item8.id == 817) {//Mặt nạ Inu
                    msg.writer().writeShort(50);
                } else {
                    msg.writer().writeShort(-1);
                }
            } else {
                msg.writer().writeShort(-1);
            }
            Item item9 = c.get().ItemBody[26];//bienhinh
            if (item9 != null) {
                if (item9.id == 825) {//Pet Bóng Ma
                    msg.writer().writeShort(61);
                } else if (item9.id == 826) {//Pet Yêu Tinh
                    msg.writer().writeShort(62);
                } else if (item9.id == 852) {
                    msg.writer().writeShort(74);
                } else {
                    msg.writer().writeShort(-1);
                }
            } else {
                msg.writer().writeShort(-1);
            }
            for (int k = 16; k < 32; ++k) {//Trang bị 2
                final Item item = c.get().ItemBody[k];
                if (item != null) {
                    msg.writer().writeShort(item.id);
                    msg.writer().writeByte(item.getUpgrade());
                    msg.writer().writeByte(item.sys);
                }
                else {
                    msg.writer().writeShort(-1);
                }
            }
            msg.writer().flush();
            p.sendMessage(msg);
            msg.cleanup();
            p.getMobMe();
            if (sendEff) {
                for (byte n = 0; n < c.get().getVeff().size(); ++n) {
                    p.addEffectMessage(c.get().getVeff().get(n));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public static void Mobstart(final User p, final int mobid, final int dame, final boolean flag) {
        Message msg = null;
        try {
            msg = new Message(-4);
            msg.writer().writeByte(mobid);
            msg.writer().writeInt(dame);
            msg.writer().writeBoolean(flag);
            msg.writer().flush();
            p.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public static void Mobstart(final User p, final int mobid, final int hp, final int dame, final boolean flag, final int levelboss, final int hpmax) {
        Message msg = null;
        try {
            msg = new Message(-1);
            msg.writer().writeByte(mobid);
            msg.writer().writeInt(hp);
            msg.writer().writeInt(dame);
            msg.writer().writeBoolean(flag);
            msg.writer().writeByte(levelboss);
            msg.writer().writeInt(hpmax);
            msg.writer().flush();
            p.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    protected static void HavePlayerAttack(Ninja _ninja, Ninja player, int dame) {
        Message msg = null;
        try {
            msg = new Message((byte) 62);
            msg.writer().writeInt(player.id);
            msg.writer().writeInt(player.hp);
            msg.writer().writeInt(dame);
            msg.writer().writeInt(player.mp);
            msg.writer().writeInt(0);
            _ninja.p.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (msg != null)
                msg.cleanup();
        }
    }


    public static void PlayerAttack(final User p, final Mob[] mob, final Body b) {
        Message msg = null;
        try {
            msg = new Message(60);
            msg.writer().writeInt(b.id);
            msg.writer().writeByte(b.getCSkill());
            for (byte i = 0; i < mob.length; ++i) {
                if (mob[i] != null) {
                    msg.writer().writeByte(mob[i].id);
                }
            }
            msg.writer().flush();
            p.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    @SneakyThrows
    public static void sendBattleList(User p) {
        val m = new Message(-156);
        val battles = Battle.battles.entrySet()
                .stream().filter(e -> e.getValue().getState() == Battle.CHIEN_DAU_STATE)
                .collect(Collectors.toList());
        m.writer().writeByte(battles.size());
        battles.forEach(e -> {
            val key = e.getKey();
            val battle = e.getValue();
            try {
                m.writer().writeByte(key);
                m.writer().writeUTF(battle.getTeam1Name());
                m.writer().writeUTF(battle.getTeam2Name());
            } catch (IOException ex) {
            }
        });
        m.writer().flush();
        p.sendMessage(m);
        m.cleanup();

    }


    ///////////////////////////////
    public static void PlayerAttack(Ninja _ninja, int charID, byte skillId, Mob[] arrMob, Ninja[] arrNinja) {
        Message msg = null;
        try {
            msg = new Message((byte) 4);
            msg.writer().writeInt(charID);
            msg.writer().writeByte(skillId);
            byte num = 0;
            byte i;
            for (i = 0; i < arrMob.length; i = (byte) (i + 1)) {
                if (arrMob[i] != null) {
                    num = (byte) (num + 1);
                }
            }
            msg.writer().writeByte(num);
            for (i = 0; i < arrMob.length; i = (byte) (i + 1)) {
                if (arrMob[i] != null) {
                    msg.writer().writeByte((arrMob[i]).templates.id);
                }
            }
            for (i = 0; i < arrNinja.length; i = (byte) (i + 1)) {
                if (arrNinja[i] != null) {
                    msg.writer().writeInt((arrNinja[i]).id);
                }
            }
            _ninja.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (msg != null)
                msg.cleanup();
        }
    }

    public static void PlayerAttack(Ninja _ninja, int charID, byte skill, Mob[] arrMob) {
        Message msg = null;
        try {
            msg = new Message((byte) 60);
            msg.writer().writeInt(charID);
            msg.writer().writeByte(skill);
            byte i;
            for (i = 0; i < arrMob.length; i = (byte) (i + 1)) {
                if (arrMob[i] != null) {
                    msg.writer().writeByte((arrMob[i]).templates.id);
                }
            }
            _ninja.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (msg != null)
                msg.cleanup();
        }
    }

    public static void PlayerAttack(Ninja _ninja, int charID, short skill, Ninja[] arrNinja) {
        Message msg = null;
        try {
            msg = new Message((byte) 61);
            msg.writer().writeInt(charID);
            msg.writer().writeByte(skill);
            byte i;
            for (i = 0; i < arrNinja.length; i = (byte) (i + 1)) {
                if (arrNinja[i] != null) {
                    msg.writer().writeInt((arrNinja[i]).id);
                }
            }
            _ninja.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (msg != null)
                msg.cleanup();
        }
    }

    @SneakyThrows
    public static void sendBattleResult(Ninja n, IBattle battle) {
        val m = messageNotMap((byte) (46 - 126));
        m.writer().writeUTF(battle.getResult(n));
        val reward = battle.getRewards(n) != null && battle.getRewards(n).length > 0 && n.getClanBattle() == null;
        m.writer().writeBoolean(reward);
        n.sendMessage(m);
        m.cleanup();
    }

    @SneakyThrows
    public static void sendThongBao(SendMessage n, String message) {
        val m = messageNotMap((byte) (46 - 126));
        m.writer().writeUTF(message);
        m.writer().writeBoolean(false);
        n.sendMessage(m);
        m.cleanup();
    }

    @SneakyThrows
    public static void sendChallenges(List<TournamentData> tournaments, SendMessage p) {
        val m = new Message(-135);
        m.writer().writeByte(tournaments.size());
        for (TournamentData tournament : tournaments) {
            m.writer().writeUTF(tournament.getName());
            m.writer().writeInt(tournament.getRanked());
            m.writer().writeUTF(tournament.getStatus());
        }
        p.sendMessage(m);
        m.cleanup();
    }

    public static void openUISay(Ninja _ninja, short npcTemplateId, String chat) {
        Message msg = null;
        try {
            msg = new Message((byte) 38);
            msg.writer().writeShort(npcTemplateId);
            msg.writer().writeUTF(chat);
            _ninja.p.session.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (msg != null)
                msg.cleanup();
        }
    }

    public static void finishTask(Ninja _ninja) {
        Message msg = null;
        try {
            msg = new Message((byte) 49);
            _ninja.p.session.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (msg != null)
                msg.cleanup();
        }
    }

    public static void updateTask(Ninja _ninja) {
        Message msg = null;
        try {
            msg = new Message((byte) 50);
            msg.writer().writeShort(_ninja.taskCount);
            _ninja.p.session.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (msg != null)
                msg.cleanup();
        }
    }


    public static void nextTask(Ninja _ninja) {
        Message msg = null;
        try {
            msg = new Message((byte) 48);
            _ninja.p.session.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (msg != null)
                msg.cleanup();
        }
    }

    public static void getTask(Ninja _ninja) {
        Message msg = null;
        try {
            if (TaskList.taskTemplates.length <= _ninja.getTaskId()) return;

            TaskTemplate taskTemplate = TaskList.taskTemplates[_ninja.getTaskId()];
            msg = new Message((byte) 47);
            msg.writer().writeShort(taskTemplate.getTaskId());
            msg.writer().writeByte(_ninja.getTaskIndex());
            msg.writer().writeUTF(taskTemplate.getName());
            msg.writer().writeUTF(taskTemplate.getDetail());
            msg.writer().writeByte(taskTemplate.subNames.length);
            for (byte b = 0; b < taskTemplate.subNames.length; b = (byte) (b + 1)) {
                msg.writer().writeUTF(taskTemplate.getSubNames()[b]);
            }
            msg.writer().writeShort(_ninja.taskCount);
            for (short i = 0; i < taskTemplate.getCounts().length; i = (short) (i + 1)) {
                msg.writer().writeShort(taskTemplate.getCounts()[i]);
            }
            _ninja.p.session.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (msg != null)
                msg.cleanup();
        }
    }

    @SneakyThrows
    public static void showWait(String s, SendMessage sendMsg) {
        final Message message = messageSubCommand2(54);
        message.writer().writeUTF(s);
        sendMsg.sendMessage(message);
    }

    public static void endWait(SendMessage sendMessage) {
        final Message message = messageSubCommand2(39);
        sendMessage.sendMessage(message);
    }

    public static void sendBallEffect(Ninja ninja) {
        val m = messageSubCommand2(70);
        ninja.sendMessage(m);
    }

    @SneakyThrows
    public static void sendThieuDot(Collection<? extends SendMessage> sendMessages, int mobId) {
        val m = messageSubCommand2(55);
        m.writer().writeByte(mobId);
        for (SendMessage sendMessage : sendMessages) {
            sendMessage.sendMessage(m);
        }
    }
}
