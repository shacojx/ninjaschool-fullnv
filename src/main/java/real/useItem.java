package real;

import boardGame.Place;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.val;
import patch.clan.ClanThanThu;
import server.*;
import tasks.TaskHandle;
import tasks.Text;
import threading.Message;
import patch.EventItem;
import threading.Server;
import threading.Map;

import static threading.Manager.*;

public class useItem {
    public static final int _1_DAY = 86400;
    public static final int _1HOUR = 3600000;
    static Server server;
    static final int[] arrOp;
    static final int[] arrParam;
    private static final byte[] arrOpenBag;
    public static final int _10_MINS = 10 * 60 * 1000;

    static {
        useItem.server = Server.getInstance();
        arrOp = new int[]{6, 7, 10, 67, 68, 69, 70, 71, 72, 73, 74};
        arrParam = new int[]{50, 50, 10, 5, 10, 10, 5, 5, 5, 100, 50};
        arrOpenBag = new byte[]{0, 6, 6, 12};
    }

    public static void uesItem(final User p, final Item item, final byte index) throws IOException {
        if (ItemData.ItemDataId(item.id).level > p.nj.get().getLevel()) {
            return;
        }
        final ItemData data = ItemData.ItemDataId(item.id);
        if (data.gender != 2 && data.gender != p.nj.gender) {
            return;
        }
        if (data.type == 26) {
            p.sendYellowMessage("Vật phẩm liên quan đến nâng cấp, hãy gặp Kenshinto trong làng để sử dụng.");
            return;
        }

        if (item.id != 194) {
            if ((p.nj.get().nclass == 0 && item.id == 547) || item.id != 400 && (data.nclass > 0 && data.nclass != p.nj.get().nclass)) {
                p.sendYellowMessage("Môn phái không phù hợp");
                return;
            }
        }

        // TODO
        if (p.nj.isNhanban && item.id == 547) {
            p.sendYellowMessage("Chức năng này không thể sử dụng cho phân thân");
            return;
        }
        if (ItemData.isTypeBody(item.id)) {
            item.setLock(true);
            Item itemb = null;
            if (item.id == 795 || item.id == 796 || item.id == 799 || item.id == 800 || item.id == 804 || item.id == 805 || item.id == 813 || item.id == 814 || item.id == 815 || item.id == 816 || item.id == 817 || item.id == 825 || item.id == 826 || item.id == 850 || item.id == 852) {
                itemb = p.nj.get().ItemBody[data.type+16];
                p.nj.ItemBag[index] = itemb;
                p.nj.get().ItemBody[data.type+16] = item;
            } else {
                itemb = p.nj.get().ItemBody[data.type];
                p.nj.ItemBag[index] = itemb;
                p.nj.get().ItemBody[data.type] = item;
            }

            if (data.type == 10) {
                p.mobMeMessage(0, (byte) 0);
            }
            if (itemb != null && itemb.id == 569) {
                p.removeEffect(36);
            }

            switch (item.id) {
                case 246: {
                    p.mobMeMessage(70, (byte) 0);
                    break;
                }
                case 419: {
                    p.mobMeMessage(122, (byte) 0);
                    break;
                }
                case 568: {
                    p.setEffect(38, 0, (int) (item.expires - System.currentTimeMillis()), 0);
                    p.mobMeMessage(205, (byte) 0);
                    break;
                }
                case 569: {
                    p.setEffect(36, 0, (int) (item.expires - System.currentTimeMillis()), p.nj.get().getPramItem(99));
                    p.mobMeMessage(206, (byte) 0);
                    break;
                }
                case 570: {
                    p.setEffect(37, 0, (int) (item.expires - System.currentTimeMillis()), 0);
                    p.mobMeMessage(207, (byte) 0);
                    break;
                }
                case 571: {
                    p.setEffect(39, 0, (int) (item.expires - System.currentTimeMillis()), 0);
                    p.mobMeMessage(208, (byte) 0);
                    break;
                }
                case 583: {
                    p.mobMeMessage(211, (byte) 1);
                    break;
                }
                case 584: {
                    p.mobMeMessage(212, (byte) 1);
                    break;
                }
                case 585: {
                    p.mobMeMessage(213, (byte) 1);
                    break;
                }
                case 586: {
                    p.mobMeMessage(214, (byte) 1);
                    break;
                }
                case 587: {
                    p.mobMeMessage(215, (byte) 1);
                    break;
                }
                case 588: {
                    p.mobMeMessage(216, (byte) 1);
                    break;
                }
                case 589: {
                    p.mobMeMessage(217, (byte) 1);
                    break;
                }
                case 742: {
                    p.mobMeMessage(229, (byte) 1);
                    break;
                }
                case 772: {
                    p.mobMeMessage(234, (byte) 1);
                    break;
                }
                case 773: {
                    p.mobMeMessage(234, (byte) 1);
                    break;
                }
                case 838: {
                    p.mobMeMessage(220, (byte) 1);
                    break;
                }
                case 837: {
                    p.mobMeMessage(223, (byte) 1);
                    break;
                }
                case 836: {
                    p.mobMeMessage(224, (byte) 1);
                    break;
                }
                case 846: {
                    p.mobMeMessage(221, (byte) 1);
                    break;
                }
                case 781: {
                    p.mobMeMessage(235, (byte) 1);
                    break;
                }
            }
        } else if (ItemData.isTypeMounts(item.id)) {
            final byte idM = (byte) (data.type - 29);
            final Item itemM = p.nj.get().ItemMounts[idM];
            if (idM == 4) {
                if (p.nj.get().ItemMounts[0] != null || p.nj.get().ItemMounts[1] != null || p.nj.get().ItemMounts[2] != null || p.nj.get().ItemMounts[3] != null) {
                    p.session.sendMessageLog("Bạn cần phải tháo trang bị thú cưới đang sử dụng");
                    return;
                }
                if (!item.isLock()) {

                    for (byte i = 0; i < 4; ++i) {
                        int attemp = 400;
                        int optionId = -1;
                        do {
                            optionId = util.nextInt(useItem.arrOp.length);
                            for (final Option option : item.option) {
                                if (useItem.arrOp[optionId] == option.id) {
                                    optionId = -1;
                                    break;
                                }
                            }
                            attemp--;
                            if (attemp <= 0) {
                                if (optionId == -1) {
                                    optionId = Arrays.stream(useItem.arrOp)
                                            .filter(id -> item.option.stream().noneMatch(o -> o.id == id))
                                            .findFirst().orElse(-1);
                                }
                                break;
                            }
                        } while (optionId == -1);
                        if (optionId == -1) return;
                        final int idOp = useItem.arrOp[optionId];
                        int par = useItem.arrParam[optionId];
                        // Soi den
                        if (item.isExpires) {
                            par *= 10;
                        }
                        final Option option2 = new Option(idOp, par);
                        item.option.add(option2);
                    }
                    if (item.id == 839) {//Phượng Hoàng Băng
                        Option option3 = new Option(134, 3);
                        item.option.add(option3);
                        option3 = new Option(135, 3);
                        item.option.add(option3);
                    }
                    if (item.id == 851) {//bach ho
                        Option option3 = new Option(58, 20);
                        item.option.add(option3);
                        option3 = new Option(94, 15);
                        item.option.add(option3);
                    }
                    if (item.id == 798 || item.id == 801 || item.id == 802 || item.id == 803) {//ngua
                        Option option3 = new Option(119, 200);
                        item.option.add(option3);
                        option3 = new Option(120, 200);
                        item.option.add(option3);
                    }
                }
            } else if (p.nj.get().ItemMounts[4] == null) {
                p.session.sendMessageLog("Bạn cần có thú cưới để sử dụng");
                return;
            }
            item.setLock(true);
            p.nj.ItemBag[index] = itemM;
            p.nj.get().ItemMounts[idM] = item;
        }
        if (data.skill > 0) {
            byte skill = data.skill;
            if (item.id == 547) {
                skill += p.nj.get().nclass;
            }
            p.openBookSkill(index, skill);
            return;
        }
        final byte numbagnull = p.nj.getAvailableBag();
        switch (item.id) {
            case 12: {
                p.nj.upyenMessage(util.nextInt((int) YEN_TA * 30 / 100, (int) YEN_TA));
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 13: {
                if (p.buffHP(25)) {
                    p.nj.removeItemBag(index, 1);
                }
                return;
            }
            case 14: {
                if (p.buffHP(90)) {
                    p.nj.removeItemBag(index, 1);
                }
                return;
            }
            case 15: {
                if (p.buffHP(230)) {
                    p.nj.removeItemBag(index, 1);
                }
                return;
            }
            case 16: {
                if (p.buffHP(400)) {
                    p.nj.removeItemBag(index, 1);
                }
                return;
            }
            case 17: {
                if (p.buffHP(650)) {
                    p.nj.removeItemBag(index, 1);
                }
                return;
            }


            case 18: {
                if (p.buffMP(150)) {
                    p.nj.removeItemBag(index, 1);
                }
                return;
            }
            case 19: {
                if (p.buffMP(500)) {
                    p.nj.removeItemBag(index, 1);
                }
                return;
            }
            case 20: {
                if (p.buffMP(1000)) {
                    p.nj.removeItemBag(index, 1);
                }
                return;
            }
            case 21: {
                if (p.buffMP(2000)) {
                    p.nj.removeItemBag(index, 1);
                }
                return;
            }
            case 22: {
                if (p.buffMP(3500)) {
                    p.nj.removeItemBag(index, 1);
                }
                return;
            }
            case 566: {
                if (p.buffMP(5000)) {
                    p.nj.removeItemBag(index, 1);
                }
                return;
            }
            case 23: {
                if (p.dungThucan((byte) 0, 3, 1800)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 24: {
                if (p.dungThucan((byte) 1, 20, 1800)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 25: {
                if (p.dungThucan((byte) 2, 30, 1800)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 26: {
                if (p.dungThucan((byte) 3, 40, 1800)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 27: {
                if (p.dungThucan((byte) 4, 50, 1800)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 29: {
                if (p.dungThucan((byte) 28, 60, 1800)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 30: {
                if (p.dungThucan((byte) 28, 60, 259200)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 34:
            case 36: {
                final Map map = getMapid(p.nj.mapLTD);
                if (map != null) {
                    for (byte i = 0; i < map.area.length; ++i) {
                        if (map.area[i].getNumplayers() < map.template.maxplayers) {
                            p.nj.getPlace().leave(p);
                            map.area[i].EnterMap0(p.nj);
                            if (item.id == 34) {
                                p.nj.removeItemBag(index, 1);
                            }
                            return;
                        }
                    }
                    break;
                }
                break;
            }
            case 222:
            case 223:
            case 224:
            case 225:
            case 226:
            case 227:
            case 228:
                if ((p.nj.quantityItemyTotal(222) < 1) || (p.nj.quantityItemyTotal(223) < 1) || (p.nj.quantityItemyTotal(224) < 1) || (p.nj.quantityItemyTotal(225) < 1) || (p.nj.quantityItemyTotal(226) < 1) || (p.nj.quantityItemyTotal(227) < 1) || (p.nj.quantityItemyTotal(228) < 1)) {
                    p.sendYellowMessage("Chưa sưu tầm đủ 7 viên ngọc rồng");
                } else if (p.nj.getAvailableBag() == 0) {
                    p.sendYellowMessage("Hành trang không đủ chỗ trống");

                }
                else {
                    p.nj.removeItemBags(222, 1);
                    p.nj.removeItemBags(223, 1);
                    p.nj.removeItemBags(224, 1);
                    p.nj.removeItemBags(225, 1);
                    p.nj.removeItemBags(226, 1);
                    p.nj.removeItemBags(227, 1);
                    p.nj.removeItemBags(228, 1);

                    Message m = new Message(-30);
                    m.writer().writeByte(-58);
                    m.writer().writeInt(p.nj.get().id);
                    m.writer().flush();
                    p.session.sendMessage(m);
                    m.cleanup();

                    Message m2 = new Message(-30);
                    m2.writer().writeByte(-57);
                    m2.writer().flush();
                    p.nj.place.sendMessage(m2);
                    m2.cleanup();
                    Item itemup = ItemData.itemDefault(420);
                    if (p.nj.get().nclass == 3 || p.nj.get().nclass == 4) {
                        itemup = ItemData.itemDefault(421);
                    } else if (p.nj.get().nclass == 5 || p.nj.get().nclass == 6) {
                        itemup = ItemData.itemDefault(422);
                    }
                    itemup.isLock = true;
                    p.nj.addItemBag(false, itemup);
                    break;
                }
                break;
            case 257: {
                if (p.nj.get().pk > 0) {
                    final Body value = p.nj.get();
                    value.pk -= 5;
                    if (p.nj.get().pk < 0) {
                        p.nj.get().pk = 0;
                    }
                    p.sendYellowMessage("Điểm hiếu chiến của bạn còn lại là " + p.nj.get().pk);
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                p.sendYellowMessage("Bạn không có điểm hiếu chiến");
                break;
            }
            case 279: {
                server.menu.sendWrite(p, (short) 1, "Nhập tên nhân vật");
                break;
            }
            case 409: {
                if (p.dungThucan((byte) 30, 75, 86400)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 410: {
                if (p.dungThucan((byte) 31, 90, 86400)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }

            case 252: {
                if (p.nj.get().getKyNangSo() >= 3) {
                    p.nj.get().setKyNangSo(3);
                    p.session.sendMessageLog("Chỉ được học tối đa 3 quyển");
                } else if (p.nj.isHuman) {
                    p.nj.get().setKyNangSo(p.nj.getKyNangSo() + 1);
                    p.nj.removeItemBag(index, 1);
                    p.nj.get().setSpoint(p.nj.getSpoint() + 1);
                    p.sendYellowMessage("Bạn nhận được 1 điểm kỹ năng");
                    p.loadSkill();
                } else if (p.nj.isNhanban && p.nj.clone != null) {
                    p.nj.get().setKyNangSo(p.nj.clone.getKyNangSo() + 1);
                    p.nj.removeItemBag(index, 1);
                    p.nj.get().setSpoint(p.nj.getSpoint() + 1);
                    p.sendYellowMessage("Bạn nhận được 1 điểm kỹ năng");
                    p.loadSkill();
                }


                break;
            }

            case 253: {
                // Hoc sach tiem nang TODO
                if (p.nj.get().getTiemNangSo() >= 8) {
                    p.nj.get().setTiemNangSo(8);
                    p.session.sendMessageLog("Chỉ được học tối đa 8 quyển");
                    break;
                } else if (p.nj.isHuman) {
                    p.nj.get().setTiemNangSo(p.nj.get().getTiemNangSo() + 1);
                    p.nj.get().updatePpoint(p.nj.get().getPpoint() + 10);
                    p.nj.removeItemBag(index, 1);
                    p.updatePotential();
                    p.sendYellowMessage("Bạn nhận được 10 điểm tiềm năng");
                } else if (p.nj.isNhanban && p.nj.clone != null) {
                    p.nj.clone.setTiemNangSo(p.nj.clone.getTiemNangSo() + 1);
                    p.nj.get().updatePpoint(p.nj.get().getPpoint() + 10);
                    p.nj.removeItemBag(index, 1);
                    p.updatePotential();
                }
                break;
            }

            case 215:
            case 229:
            case 283: {
                final byte level = (byte) ((item.id != 215) ? ((item.id != 229) ? 3 : 2) : 1);
                if (level > p.nj.levelBag + 1) {
                    p.sendYellowMessage("Cần mở Túi vải cấp " + (p.nj.levelBag + 1) + " mới có thể mở được túi vải này");
                    return;
                }
                if (p.nj.levelBag >= level) {
                    p.sendYellowMessage("Bạn đã mở túi vải này rồi");
                    return;
                }
                p.nj.levelBag = level;
                final Ninja c = p.nj;
                c.maxluggage += useItem.arrOpenBag[level];
                final Item[] bag = new Item[p.nj.maxluggage];
                for (int j = 0; j < p.nj.ItemBag.length; ++j) {
                    bag[j] = p.nj.ItemBag[j];
                }
                (p.nj.ItemBag = bag)[index] = null;
                p.openBagLevel(index);
                break;
            }
            case 272: {
                // Rương may mắn
                if (numbagnull == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    return;
                }
                if (util.nextInt(2) == 0) {
                    final int num = util.nextInt(MIN_MAX_YEN_RUONG_MAY_MAN[0], MIN_MAX_YEN_RUONG_MAY_MAN[1]);
                    p.nj.upyenMessage(num);
                    p.sendYellowMessage("Bạn nhận được " + num + " yên");
                } else {
                    final short[] arId = {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 242, 275, 276, 277, 278, 280, 284};
                    final short idI = arId[util.nextInt(arId.length)];
                    final ItemData data2 = ItemData.ItemDataId(idI);
                    Item itemup;
                    if (data2.type < 10) {
                        if (data2.type == 1) {
                            itemup = ItemData.itemDefault(idI);
                            itemup.sys = GameScr.SysClass(data2.nclass);
                        } else {
                            final byte sys = (byte) util.nextInt(1, 3);
                            itemup = ItemData.itemDefault(idI, sys);
                        }
                    } else {
                        itemup = ItemData.itemDefault(idI);
                    }
                    itemup.setLock(item.isLock());
                    for (final Option Option : itemup.option) {
                        final int idOp2 = Option.id;
                        Option.param = util.nextInt(item.getOptionShopMin(idOp2, Option.param), Option.param);
                    }
                    p.nj.addItemBag(true, itemup);
                }
                if (p.nj.getTaskId() == 40 && p.nj.getTaskIndex() == 1) {
                    p.nj.upMainTask();
                }
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 248: {
                final Effect eff = p.nj.get().getEffId(22);
                if (eff != null) {
                    final long time = eff.timeRemove + 18000000L;
                    p.setEffect(22, 0, (int) (time - System.currentTimeMillis()), 2);
                } else {
                    p.setEffect(22, 0, 18000000, 2);
                }
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 276: {
                // Long luc dan
                p.setEffect(25, 0, 600000, 500);
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 275: {
                // Minh man dan
                p.setEffect(24, 0, _10_MINS, 500);
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 277: {
                // Khang the dan
                p.setEffect(26, 0, _10_MINS, 100);
                p.nj.removeItemBag(index, 1);
                break;

            }
            case 278: {
                // SInh menh dan
                p.setEffect(29, 0, _10_MINS, 1000);
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 280: {
                // TODO HD COUNT
                if (p.nj.useCave == 0) {
                    p.session.sendMessageLog("Số lần dùng Lệnh bài hạng động trong ngày hôm nay đã hết");
                    return;
                }
                final Ninja c2 = p.nj;
                ++c2.nCave;
                final Ninja c3 = p.nj;
                --c3.useCave;
                p.sendYellowMessage("Số lần đi hang động của bạn trong ngày hôm nay tăng lên là " + p.nj.nCave + " lần");
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 282: {
                if (numbagnull == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    return;
                }
                if (util.nextInt(2) == 0) {
                    final int num = util.nextInt(MIN_MAX_YEN_RUONG_TINH_SAO[0], MIN_MAX_YEN_RUONG_TINH_SAO[1]);
                    p.nj.upyenMessage(num);
                    p.sendYellowMessage("Bạn nhận được " + num + " yên");
                } else {
                    final short[] arId = {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7, 8, 8, 8, 9, 9, 9, 10, 10, 11, 242, 275, 276, 277, 278, 280, 280, 280, 283, 284, 436, 437};
                    final short idI = arId[util.nextInt(arId.length)];
                    final ItemData data2 = ItemData.ItemDataId(idI);
                    Item itemup;
                    if (data2.type < 10) {
                        if (data2.type == 1) {
                            itemup = ItemData.itemDefault(idI);
                            itemup.sys = GameScr.SysClass(data2.nclass);
                        } else {
                            final byte sys = (byte) util.nextInt(1, 3);
                            itemup = ItemData.itemDefault(idI, sys);
                        }
                    } else {
                        itemup = ItemData.itemDefault(idI);
                    }
                    itemup.setLock(item.isLock());
                    for (final Option Option : itemup.option) {
                        final int idOp2 = Option.id;
                        Option.param = util.nextInt(item.getOptionShopMin(idOp2, Option.param), Option.param);
                    }
                    p.nj.addItemBag(true, itemup);
                }
                p.nj.removeItemBag(index, 1);
                if (p.nj.getTaskId() == 40 && p.nj.getTaskIndex() == 1) {
                    p.nj.upMainTask();
                }
                break;
            }
            case 308: {
                // Phong loi
                if (p.nj.get().getPhongLoi() >= 10) {
                    p.nj.get().setPhongLoi(10);
                    p.session.sendMessageLog("Chi được dùng tối đa 10 cái");
                } else if (p.nj.isHuman) {
                    p.nj.get().setPhongLoi(p.nj.get().getPhongLoi() + 1);
                    p.nj.removeItemBag(index, 1);
                    p.nj.get().setSpoint(p.nj.get().getSpoint() + 1);
                    p.sendYellowMessage("Bạn nhận được 1 điểm kỹ năng");
                    p.loadSkill();
                } else if (p.nj.isNhanban) {
                    if (p.nj.clone != null) {
                        p.nj.clone.setPhongLoi(p.nj.clone.getPhongLoi() + 1);
                        p.nj.get().setSpoint(p.nj.get().getSpoint() + 1);
                        p.nj.removeItemBag(index, 1);
                        p.sendYellowMessage("Bạn nhận được 1 điểm kỹ năng");
                        p.loadSkill();
                    }
                }
                break;
            }
            case 309: {
                if (p.nj.get().getBanghoa() >= 10) {
                    p.nj.get().setBanghoa(10);
                    p.session.sendMessageLog("Chi được dùng tối đa 10 cái");
                } else if (p.nj.isHuman) {
                    p.nj.get().setBanghoa(p.nj.get().getBanghoa() + 1);
                    p.nj.get().updatePpoint(p.nj.getPpoint() + 10);
                    p.nj.removeItemBag(index, 1);
                    p.updatePotential();
                    p.sendYellowMessage("Bạn nhận được 10 điểm tiềm năng");
                } else if (p.nj.isNhanban) {
                    if (p.nj.clone != null) {
                        p.nj.clone.setBanghoa(p.nj.clone.getBanghoa() + 1);
                        p.nj.updatePpoint(p.nj.getPpoint() + 10);
                        p.nj.removeItemBag(index, 1);
                        p.updatePotential();
                        p.sendYellowMessage("Bạn nhận được 10 điểm tiềm năng");
                    }
                }
                // Bang hoa
                break;
            }
            case 383:
            case 384:
            case 385: {
                if (numbagnull == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    return;
                }
                if (p.nj.get().nclass == 0) {
                    p.session.sendMessageLog("Hãy nhập học để mở vật phẩm.");
                    return;
                }
                byte sys2 = -1;
                int idI2;
                if (util.nextInt(2) == 0) {
                    if (p.nj.gender == 0) {
                        if (p.nj.get().getLevel() < 50 && item.id != 384 && item.id != 385) {
                            idI2 = (new short[]{171, 161, 151, 141, 131})[util.nextInt(5)];
                        } else if (p.nj.get().getLevel() < 60 && item.id != 385) {
                            idI2 = (new short[]{137, 163, 153, 143, 133})[util.nextInt(5)];
                        } else if (p.nj.get().getLevel() < 70) {
                            idI2 = (new short[]{330, 329, 328, 327, 326})[util.nextInt(5)];
                        } else {
                            idI2 = (new short[]{368, 367, 366, 365, 364})[util.nextInt(5)];
                        }
                    } else if (p.nj.get().getLevel() < 50 && item.id != 384 && item.id != 385) {
                        idI2 = (new short[]{170, 160, 102, 140, 130})[util.nextInt(5)];
                    } else if (p.nj.get().getLevel() < 60 && item.id != 385) {
                        idI2 = (new short[]{172, 162, 103, 142, 132})[util.nextInt(5)];
                    } else if (p.nj.get().getLevel() < 70) {
                        idI2 = (new short[]{325, 323, 333, 319, 317})[util.nextInt(5)];
                    } else {
                        idI2 = (new short[]{363, 361, 359, 357, 355})[util.nextInt(5)];
                    }
                } else if (util.nextInt(2) == 1) {
                    if (p.nj.get().nclass == 1 || p.nj.get().nclass == 2) {
                        sys2 = 1;
                    } else if (p.nj.get().nclass == 3 || p.nj.get().nclass == 4) {
                        sys2 = 2;
                    } else if (p.nj.get().nclass == 5 || p.nj.get().nclass == 6) {
                        sys2 = 3;
                    }
                    if (p.nj.get().getLevel() < 50 && item.id != 384 && item.id != 385) {
                        idI2 = (new short[]{97, 117, 102, 112, 107, 122})[p.nj.get().nclass - 1];
                    } else if (p.nj.get().getLevel() < 60 && item.id != 385) {
                        idI2 = (new short[]{98, 118, 103, 113, 108, 123})[p.nj.get().nclass - 1];
                    } else if (p.nj.get().getLevel() < 70) {
                        idI2 = (new short[]{331, 332, 333, 334, 335, 336})[p.nj.get().nclass - 1];
                    } else {
                        idI2 = (new short[]{369, 370, 371, 372, 373, 374})[p.nj.get().nclass - 1];
                    }
                } else if (p.nj.get().getLevel() < 50 && item.id != 384 && item.id != 385) {
                    idI2 = (new short[]{192, 187, 182, 177})[util.nextInt(4)];
                } else if (p.nj.get().getLevel() < 60 && item.id != 385) {
                    idI2 = (new short[]{193, 188, 183, 178})[util.nextInt(4)];
                } else if (p.nj.get().getLevel() < 70) {
                    idI2 = (new short[]{324, 322, 320, 318})[util.nextInt(4)];
                } else {
                    idI2 = (new short[]{362, 360, 358, 356})[util.nextInt(4)];
                }
                Item itemup;
                if (sys2 < 0) {
                    sys2 = (byte) util.nextInt(1, 3);
                    itemup = ItemData.itemDefault(idI2, sys2);
                } else {
                    itemup = ItemData.itemDefault(idI2);
                }
                itemup.sys = sys2;
                byte nextup = 12;
                if (item.id == 384) {
                    nextup = 14;
                } else if (item.id == 385) {
                    nextup = 16;
                }
                itemup.setLock(item.isLock());
                itemup.upgradeNext(nextup);
                p.nj.addItemBag(true, itemup);
                p.nj.removeItemBag(index, 1);
                break;
            }

            case 436:
            case 437:
            case 438: {
                final ClanManager clan = ClanManager.getClanByName(p.nj.clan.clanName);
                if (clan == null || clan.getMem(p.nj.name) == null) {
                    p.sendYellowMessage("Cần có gia tộc để sử dụng");
                    return;
                }
                if (item.id == 436) {
                    if (clan.getLevel() < 1) {
                        p.sendYellowMessage("Yêu cầu gia tộc phải đạt cấp 5");
                        return;
                    }
                    p.upExpClan(util.nextInt(100, 200));
                    p.nj.removeItemBag(index, 1);
                    return;
                } else if (item.id == 437) {
                    if (clan.getLevel() < 10) {
                        p.sendYellowMessage("Yêu cầu gia tộc phải đạt cấp 10");
                        return;
                    }
                    p.upExpClan(util.nextInt(300, 800));
                    p.nj.removeItemBag(index, 1);
                    return;
                } else {
                    if (item.id != 438) {
                        break;
                    }
                    if (clan.getLevel() < 15) {
                        p.sendYellowMessage("Yêu cầu gia tộc phải đạt cấp 15");
                        return;
                    }
                    p.upExpClan(util.nextInt(1000, 2000));
                    p.nj.removeItemBag(index, 1);
                    return;
                }
            }
            case 449: {
                if (p.updateXpMounts(5,(byte)0))
                    p.nj.removeItemBag(index, 1);
            }
            break;
            case 450: {
                if (p.updateXpMounts(7,(byte)0))
                    p.nj.removeItemBag(index, 1);
            }
            break;
            case 451: {
                if (p.updateXpMounts(14,(byte)0))
                    p.nj.removeItemBag(index, 1);
            }
            break;
            case 452: {
                if (p.updateXpMounts(20,(byte)0))
                    p.nj.removeItemBag(index, 1);
            }
            break;
            case 453: {
                if (p.updateXpMounts(25,(byte)0))
                    p.nj.removeItemBag(index, 1);
            }
            break;
            case 778: {
                if (p.updateXpMounts(util.nextInt(1, 10), (byte) 2)) {
                    p.nj.removeItemBag(index, 1);
                }
            }
            break;
            case 454: {
                if (p.updateSysMounts()) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 268: {
                final Ninja c2 = p.nj;
                ++c2.taThuCount;
                p.sendYellowMessage("Số lần tà thú của bạn trong ngày hôm nay tăng lên là " + p.nj.taThuCount + " lần");
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 477: {
                if (numbagnull == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    return;
                } else {
                    final short[] arId = {652, 653, 654, 655};
                    final short idI = arId[util.nextInt(arId.length)];
                    final ItemData data2 = ItemData.ItemDataId(idI);
                    Item itemup;
                    if (data2.type < 10) {
                        if (data2.type == 1) {
                            itemup = ItemData.itemDefault(idI);
                            itemup.sys = GameScr.SysClass(data2.nclass);
                        } else {
                            final byte sys = (byte) util.nextInt(1, 3);
                            itemup = ItemData.itemDefault(idI, sys);
                        }
                    } else {
                        itemup = ItemData.itemDefault(idI);
                    }
                    itemup.setLock(item.isLock());
                    for (final Option Option : itemup.option) {
                        final int idOp2 = Option.id;
                        Option.param = util.nextInt(item.getOptionShopMin(idOp2, Option.param), Option.param);
                    }
                    p.nj.addItemBag(true, itemup);
                }
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 490: {
                if (p.nj.isNhanban) {
                    p.session.sendMessageLog("Chức năng này không dành cho phân thân");
                    return;
                }
                p.nj.getPlace().leave(p);
                final Map map = Server.getMapById(138);
                map.area[0].EnterMap0(p.nj);
                p.endLoad(true);
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 842: {
                if (p.nj.isNhanban) {
                    p.session.sendMessageLog("Chức năng này không dành cho phân thân");
                    return;
                }
                p.nj.getPlace().leave(p);
                final Map map = Server.getMapById(75);
                map.area[0].EnterMap0(p.nj);
                p.endLoad(true);
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 831: {// Mở ra cải trang ngộ không
                if (numbagnull == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    return;
                } else {
                    final short[] arId = {829,830};
                    final short idI = arId[util.nextInt(arId.length)];
                    final ItemData data2 = ItemData.ItemDataId(idI);
                    Item itemup;
                    if (data2.type < 10) {
                        if (data2.type == 1) {
                            itemup = ItemData.itemDefault(idI);
                            itemup.sys = GameScr.SysClass(data2.nclass);
                        } else {
                            final byte sys = (byte) util.nextInt(1, 3);
                            itemup = ItemData.itemDefault(idI, sys);
                            itemup.setUpgrade(16);
                        }
                    } else {
                        itemup = ItemData.itemDefault(idI);
                    }
                    //itemup.setLock(item.isLock());
                    //for (final Option Option : itemup.option) {
                        //final int idOp2 = Option.id;
                        //Option.param = util.nextInt(item.getOptionShopMin(idOp2, Option.param), Option.param);
                    //}
                    p.nj.addItemBag(true, itemup);
                }
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 537: {
                // Khai nhan phu
                val id = 41;
                final Effect eff = p.nj.get().getEffId(id);
                if (eff != null) {
                    final long time = eff.timeRemove + _1HOUR * 3;
                    p.setEffect(id, 0, (int) (time - System.currentTimeMillis()), 2);
                } else {
                    p.setEffect(id, 0, _1HOUR * 3, 2);
                }
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 538: {
                // Thien nhan phu
                val id = 40;
                final Effect eff = p.nj.get().getEffId(id);
                if (eff != null) {
                    final long time = eff.timeRemove + _1HOUR * 5;
                    p.setEffect(id, 0, (int) (time - System.currentTimeMillis()), 2);
                } else {
                    p.setEffect(id, 0, _1HOUR * 5, 2);
                }
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 565: {
                if (p.buffHP(1500)) {
                    p.nj.removeItemBag(index, 1);
                }
                return;
            }
            case 539: {
                p.setEffect(32, 0, 3600000, 3);
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 540: {
                p.setEffect(33, 0, 3600000, 4);
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 573: {
                if (p.updateXpMounts(200, (byte) 0)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 574: {
                if (p.updateXpMounts(400, (byte) 0)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 575: {
                if (p.updateXpMounts(600, (byte) 0)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 576: {
                if (p.updateXpMounts(100, (byte) 1)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 577: {
                if (p.updateXpMounts(250, (byte) 1)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 578: {
                if (p.updateXpMounts(500, (byte) 1)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 564: {
                final Effect eff = p.nj.get().getEffId(34);
                if (eff != null) {
                    final long time = eff.timeRemove + 7200000L;
                    p.setEffect(34, 0, (int) (time - System.currentTimeMillis()), 2);
                } else {
                    p.setEffect(34, 0, 7200000, 2);
                }
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 647: {
                if (numbagnull == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    return;
                }
                if (util.nextInt(2) == 0) {
                    final int num = util.nextInt(MIN_MAX_YEN_RUONG_MA_QUAI[0], MIN_MAX_YEN_RUONG_MA_QUAI[1]);
                    p.nj.upyenMessage(num);
                    p.sendYellowMessage("Bạn nhận được " + num + " yên");
                } else {
                    final short[] arId = {3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 8, 8, 8, 9, 9, 9, 10, 10, 11, 280, 280, 280, 436, 437, 539, 540, 618, 619, 620, 621, 622, 623, 624, 625, 626, 627, 628, 629, 630, 631, 632, 633, 634, 635, 636, 637};
                    final short idI = arId[util.nextInt(arId.length)];
                    final ItemData data2 = ItemData.ItemDataId(idI);
                    Item itemup;
                    if (data2.type < 10) {
                        if (data2.type == 1) {
                            itemup = ItemData.itemDefault(idI);
                            itemup.sys = GameScr.SysClass(data2.nclass);
                        } else {
                            final byte sys = (byte) util.nextInt(1, 3);
                            itemup = ItemData.itemDefault(idI, sys);
                        }
                    } else {
                        itemup = ItemData.itemDefault(idI);
                    }
                    itemup.setLock(item.isLock());
                    for (final Option Option : itemup.option) {
                        final int idOp2 = Option.id;
                        Option.param = util.nextInt(item.getOptionShopMin(idOp2, Option.param), Option.param);
                    }
                    p.nj.addItemBag(true, itemup);
                }
                if (p.nj.getTaskId() == 40 && p.nj.getTaskIndex() == 1) {
                    p.nj.upMainTask();
                }
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 249: {
                if (p.dungThucan((byte) 3, 40, _1_DAY * 3)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 250: {
                if (p.dungThucan((byte) 4, 50, _1_DAY * 3)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 567: {
                if (p.dungThucan((byte) 35, 120, _1_DAY)) {
                    p.nj.removeItemBag(index, 1);
                    break;
                }
                break;
            }
            case 251: // Mảnh giấy vụn
                p.typemenu = 850;
                MenuController.doMenuArray(p, new String[]{"Đổi sách tiềm năng", "Đổi sách kĩ năng"});
                break;
            case 775: // Hoa Tuyết
                p.typemenu = 849;
                MenuController.doMenuArray(p, new String[]{"Đổi Santa Claus"});
                break;
            case 256: {
                // Tay am cap 60 tl
                if (p.nj.get().getLevel() >= 60 && p.nj.get().expdown != 0) {
                    p.upExpDown(p.nj.get().expdown);
                    p.nj.removeItemBag(index, 1);
                } else {
                    p.sendYellowMessage("Trình độ không phù hợp hoặc bạn không có exp âm");
                }
                break;
            }

            case 255: {
                // Tay am duoi cap 60
                if (p.nj.get().getLevel() < 60 && p.nj.get().expdown != 0) {
                    p.upExpDown(p.nj.get().expdown);
                    p.nj.removeItemBag(index, 1);
                } else {
                    p.sendYellowMessage("Trình độ không phù hợp hoặc bạn không có exp âm");
                }
                break;
            }
            case 254: {
                // Tay tam duoi cap 30
                if (p.nj.get().getLevel() < 30 && p.nj.get().expdown != 0) {
                    p.upExpDown(p.nj.get().expdown);
                    p.nj.removeItemBag(index, 1);
                } else {
                    p.sendYellowMessage("Trình độ không phù hợp hoặc bạn không có exp âm");
                }
                break;
            }
            case 261: {
                // Dung linh dan danh boss
                p.setEffect(23, 0, _10_MINS, 0);
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 263: {
                // Sử dụng tui quà gia tộc
                if (numbagnull == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    return;
                }
                if (util.nextInt(2) == 0) {
                    final int num = util.nextInt(MIN_MAX_YEN_RUONG_MA_QUAI[0], MIN_MAX_YEN_RUONG_MA_QUAI[1]);
                    p.nj.upyenMessage(num);
                    p.sendYellowMessage("Bạn nhận được " + num + " yên");
                } else {
                    final short[] arId = {539,540,443,685,695,695,696};
                    final short idI = arId[util.nextInt(arId.length)];
                    final ItemData data2 = ItemData.ItemDataId(idI);
                    Item itemup;
                    if (data2.type < 10) {
                        if (data2.type == 1) {
                            itemup = ItemData.itemDefault(idI);
                            itemup.sys = GameScr.SysClass(data2.nclass);
                        } else {
                            final byte sys = (byte) util.nextInt(1, 3);
                            itemup = ItemData.itemDefault(idI, sys);
                        }
                    } else {
                        itemup = ItemData.itemDefault(idI);
                    }
                    itemup.setLock(item.isLock());
                    for (final Option Option : itemup.option) {
                        final int idOp2 = Option.id;
                        Option.param = util.nextInt(item.getOptionShopMin(idOp2, Option.param), Option.param);
                    }
                    p.nj.addItemBag(true, itemup);
                }
                if (p.nj.getTaskId() == 40 && p.nj.getTaskIndex() == 1) {
                    p.nj.upMainTask();
                }
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 572: {
                // TBL
                p.typemenu = 572;
                if (!p.activeTBL) {
                    MenuController.doMenuArray(p, new String[]{"Phạm vi 240", "Phạm vi 480", "Phạm vi toàn map", "Nhật tất cả", "Nhật vp hữu dụng", "Bật tàn sát"});
                } else {
                    MenuController.doMenuArray(p, new String[]{"Phạm vi 240", "Phạm vi 480", "Phạm vi toàn map", "Nhật tất cả", "Nhật vp hữu dụng", "Tắt tàn sát"});
                }

                break;
            }
            case 733: {
                if (p.nj.quantityItemyTotal(733) < 1000) {
                    p.session.sendMessageLog("Bạn chưa đủ 1000 mảnh nón Jirai");
                    break;
                    } else if (p.luong < 10000) {
                    p.session.sendMessageLog("Bạn chưa đủ 10000 lượng để đổi");
                    break;
                    } else if (p.nj.getAvailableBag() == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    } else {
                    Item it = ItemData.itemDefault(746, (byte) util.nextInt(1, 3));
                        it.setUpgrade(16);
                        p.nj.addItemBag(true, it);
                        p.upluongMessage(-10000);
                        p.nj.removeItemBags(733, 1000);
                           break;
                            }
                        }
                    break;
            case 734: {
                if (p.nj.quantityItemyTotal(734) < 1000) {
                    p.session.sendMessageLog("Bạn chưa đủ 1000 mảnh găng tay Jirai");
                    break;
                    } else if (p.luong < 10000) {
                    p.session.sendMessageLog("Bạn chưa đủ 10000 lượng để đổi");
                    break;
                    } else if (p.nj.getAvailableBag() == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    } else {
                    Item it = ItemData.itemDefault(747, (byte) util.nextInt(1, 3));
                        it.setUpgrade(16);
                        p.nj.addItemBag(true, it);
                        p.upluongMessage(-10000);
                        p.nj.removeItemBags(734, 1000);
                           break;
                            }
                        }
                    break;
            case 735: {
                if (p.nj.quantityItemyTotal(735) < 1000) {
                    p.session.sendMessageLog("Bạn chưa đủ 1000 mảnh áo Jirai");
                    break;
                    } else if (p.luong < 10000) {
                    p.session.sendMessageLog("Bạn chưa đủ 10000 lượng để đổi");
                    break;
                    } else if (p.nj.getAvailableBag() == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    } else {
                    Item it = ItemData.itemDefault(712, (byte) util.nextInt(1, 3));
                        it.setUpgrade(16);
                        p.nj.addItemBag(true, it);
                        p.upluongMessage(-10000);
                        p.nj.removeItemBags(735, 1000);
                           break;
                            }
                        }
                    break;   
            case 736: {
                if (p.nj.quantityItemyTotal(736) < 1000) {
                    p.session.sendMessageLog("Bạn chưa đủ 1000 mảnh quần Jirai");
                    break;
                    } else if (p.luong < 10000) {
                    p.session.sendMessageLog("Bạn chưa đủ 10000 lượng để đổi");
                    break;
                    } else if (p.nj.getAvailableBag() == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    } else {
                    Item it = ItemData.itemDefault(713, (byte) util.nextInt(1, 3));
                        it.setUpgrade(16);
                        p.nj.addItemBag(true, it);
                        p.upluongMessage(-10000);
                        p.nj.removeItemBags(736, 1000);
                           break;
                            }
                        }
                    break;
            case 737: {
                if (p.nj.quantityItemyTotal(737) < 1000) {
                    p.session.sendMessageLog("Bạn chưa đủ 1000 mảnh giày Jirai");
                    break;
                    } else if (p.luong < 10000) {
                    p.session.sendMessageLog("Bạn chưa đủ 10000 lượng để đổi");
                    break;
                    } else if (p.nj.getAvailableBag() == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    } else {
                    Item it = ItemData.itemDefault(748, (byte) util.nextInt(1, 3));
                        it.setUpgrade(16);
                        p.nj.addItemBag(true, it);
                        p.upluongMessage(-10000);
                        p.nj.removeItemBags(737, 1000);
                           break;
                            }
                        }
                    break;
            case 738: {
                if (p.nj.quantityItemyTotal(738) < 1000) {
                    p.session.sendMessageLog("Bạn chưa đủ 1000 mảnh dây chuyền Jirai");
                    break;
                    } else if (p.luong < 10000) {
                    p.session.sendMessageLog("Bạn chưa đủ 10000 lượng để đổi");
                    break;
                    } else if (p.nj.getAvailableBag() == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    } else {
                    Item it = ItemData.itemDefault(752, (byte) util.nextInt(1, 3));
                        it.setUpgrade(16);
                        p.nj.addItemBag(true, it);
                        p.upluongMessage(-10000);
                        p.nj.removeItemBags(738, 1000);
                           break;
                            }
                        }
                    break; 
            case 739: {
                if (p.nj.quantityItemyTotal(739) < 1000) {
                    p.session.sendMessageLog("Bạn chưa đủ 1000 mảnh ngọc bội Jirai");
                    break;
                    } else if (p.luong < 10000) {
                    p.session.sendMessageLog("Bạn chưa đủ 10000 lượng để đổi");
                    break;
                    } else if (p.nj.getAvailableBag() == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    } else {
                    Item it = ItemData.itemDefault(751, (byte) util.nextInt(1, 3));
                        it.setUpgrade(16);
                        p.nj.addItemBag(true, it);
                        p.upluongMessage(-10000);
                        p.nj.removeItemBags(739, 1000);
                           break;
                            }
                        }
                    break;
            case 740: {
                if (p.nj.quantityItemyTotal(740) < 1000) {
                    p.session.sendMessageLog("Bạn chưa đủ 1000 mảnh phù Jirai");
                    break;
                    } else if (p.luong < 10000) {
                    p.session.sendMessageLog("Bạn chưa đủ 10000 lượng để đổi");
                    break;
                    } else if (p.nj.getAvailableBag() == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    } else {
                    Item it = ItemData.itemDefault(750, (byte) util.nextInt(1, 3));
                        it.setUpgrade(16);
                        p.nj.addItemBag(true, it);
                        p.upluongMessage(-10000);
                        p.nj.removeItemBags(740, 1000);
                           break;
                            }
                        }
                    break;  
            case 741: {
                if (p.nj.quantityItemyTotal(741) < 1000) {
                    p.session.sendMessageLog("Bạn chưa đủ 1000 mảnh nhẫn Jirai");
                    break;
                    } else if (p.luong < 10000) {
                    p.session.sendMessageLog("Bạn chưa đủ 10000 lượng để đổi");
                    break;
                    } else if (p.nj.getAvailableBag() == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    } else {
                    Item it = ItemData.itemDefault(749, (byte) util.nextInt(1, 3));
                        it.setUpgrade(16);
                        p.nj.addItemBag(true, it);
                        p.upluongMessage(-10000);
                        p.nj.removeItemBags(741, 1000);
                           break;
                            }
                        }
                    break;  
            case 760: {
                if (p.nj.quantityItemyTotal(760) < 1000) {
                    p.session.sendMessageLog("Bạn chưa đủ 1000 mảnh nón Jumito");
                    break;
                    } else if (p.luong < 10000) {
                    p.session.sendMessageLog("Bạn chưa đủ 10000 lượng để đổi");
                    break;
                    } else if (p.nj.getAvailableBag() == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    } else {
                    Item it = ItemData.itemDefault(753, (byte) util.nextInt(1, 3));
                        it.setUpgrade(16);
                        p.nj.addItemBag(true, it);
                        p.upluongMessage(-10000);
                        p.nj.removeItemBags(760, 1000);
                           break;
                            }
                        }
                    break;    
            case 761: {
                if (p.nj.quantityItemyTotal(761) < 1000) {
                    p.session.sendMessageLog("Bạn chưa đủ 1000 mảnh găng tay Jumito");
                    break;
                    } else if (p.luong < 10000) {
                    p.session.sendMessageLog("Bạn chưa đủ 10000 lượng để đổi");
                    break;
                    } else if (p.nj.getAvailableBag() == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    } else {
                    Item it = ItemData.itemDefault(754, (byte) util.nextInt(1, 3));
                        it.setUpgrade(16);
                        p.nj.addItemBag(true, it);
                        p.upluongMessage(-10000);
                        p.nj.removeItemBags(761, 1000);
                           break;
                            }
                        }
                    break; 
            case 762: {
                if (p.nj.quantityItemyTotal(762) < 1000) {
                    p.session.sendMessageLog("Bạn chưa đủ 1000 mảnh áo Jumito");
                    break;
                    } else if (p.luong < 10000) {
                    p.session.sendMessageLog("Bạn chưa đủ 10000 lượng để đổi");
                    break;
                    } else if (p.nj.getAvailableBag() == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    } else {
                    Item it = ItemData.itemDefault(715, (byte) util.nextInt(1, 3));
                        it.setUpgrade(16);
                        p.nj.addItemBag(true, it);
                        p.upluongMessage(-10000);
                        p.nj.removeItemBags(762, 1000);
                           break;
                            }
                        }
                    break;
            case 763: {
                if (p.nj.quantityItemyTotal(763) < 1000) {
                    p.session.sendMessageLog("Bạn chưa đủ 1000 mảnh quần Jumito");
                    break;
                    } else if (p.luong < 10000) {
                    p.session.sendMessageLog("Bạn chưa đủ 10000 lượng để đổi");
                    break;
                    } else if (p.nj.getAvailableBag() == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    } else {
                    Item it = ItemData.itemDefault(716, (byte) util.nextInt(1, 3));
                        it.setUpgrade(16);
                        p.nj.addItemBag(true, it);
                        p.upluongMessage(-10000);
                        p.nj.removeItemBags(763, 1000);
                           break;
                            }
                        }
                    break;
            case 764: {
                if (p.nj.quantityItemyTotal(764) < 1000) {
                    p.session.sendMessageLog("Bạn chưa đủ 1000 mảnh giày Jumito");
                    break;
                    } else if (p.luong < 10000) {
                    p.session.sendMessageLog("Bạn chưa đủ 10000 lượng để đổi");
                    break;
                    } else if (p.nj.getAvailableBag() == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    } else {
                    Item it = ItemData.itemDefault(755, (byte) util.nextInt(1, 3));
                        it.setUpgrade(16);
                        p.nj.addItemBag(true, it);
                        p.upluongMessage(-10000);
                        p.nj.removeItemBags(764, 1000);
                           break;
                            }
                        }
                    break; 
            case 765: {
                if (p.nj.quantityItemyTotal(765) < 1000) {
                    p.session.sendMessageLog("Bạn chưa đủ 1000 mảnh dây chuyền Jumito");
                    break;
                    } else if (p.luong < 10000) {
                    p.session.sendMessageLog("Bạn chưa đủ 10000 lượng để đổi");
                    break;
                    } else if (p.nj.getAvailableBag() == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    } else {
                    Item it = ItemData.itemDefault(759, (byte) util.nextInt(1, 3));
                        it.setUpgrade(16);
                        p.nj.addItemBag(true, it);
                        p.upluongMessage(-10000);
                        p.nj.removeItemBags(765, 1000);
                           break;
                            }
                        }
                    break;
            case 766: {
                if (p.nj.quantityItemyTotal(766) < 1000) {
                    p.session.sendMessageLog("Bạn chưa đủ 1000 mảnh ngọc bội Jumito");
                    break;
                    } else if (p.luong < 10000) {
                    p.session.sendMessageLog("Bạn chưa đủ 10000 lượng để đổi");
                    break;
                    } else if (p.nj.getAvailableBag() == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    } else {
                    Item it = ItemData.itemDefault(758, (byte) util.nextInt(1, 3));
                        it.setUpgrade(16);
                        p.nj.addItemBag(true, it);
                        p.upluongMessage(-10000);
                        p.nj.removeItemBags(766, 1000);
                           break;
                            }
                        }
                    break;
            case 767: {
                if (p.nj.quantityItemyTotal(767) < 1000) {
                    p.session.sendMessageLog("Bạn chưa đủ 1000 mảnh phù Jumito");
                    break;
                    } else if (p.luong < 10000) {
                    p.session.sendMessageLog("Bạn chưa đủ 10000 lượng để đổi");
                    break;
                    } else if (p.nj.getAvailableBag() == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    } else {
                    Item it = ItemData.itemDefault(757, (byte) util.nextInt(1, 3));
                        it.setUpgrade(16);
                        p.nj.addItemBag(true, it);
                        p.upluongMessage(-10000);
                        p.nj.removeItemBags(767, 1000);
                           break;
                            }
                        }
                    break;
            case 768: {
                if (p.nj.quantityItemyTotal(768) < 1000) {
                    p.session.sendMessageLog("Bạn chưa đủ 1000 mảnh nhẫn Jumito");
                    break;
                    } else if (p.luong < 10000) {
                    p.session.sendMessageLog("Bạn chưa đủ 10000 lượng để đổi");
                    break;
                    } else if (p.nj.getAvailableBag() == 0) {
                    p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                    } else {
                    Item it = ItemData.itemDefault(756, (byte) util.nextInt(1, 3));
                        it.setUpgrade(16);
                        p.nj.addItemBag(true, it);
                        p.upluongMessage(-10000);
                        p.nj.removeItemBags(768, 1000);
                           break;
                            }
                        }
                    break;    
            case 549: {
                // Giày rách
                final int num = util.nextInt(200000, 500000);
                p.nj.upyenMessage(num);
                p.sendYellowMessage("Bạn nhận được " + num + " yên");
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 550: {
                // Giày bạc
                final int num = util.nextInt(500000, 1000000);
                p.nj.upyenMessage(num);
                p.sendYellowMessage("Bạn nhận được " + num + " yên");
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 551: {
                // Giày vàng
                final int num = util.nextInt(1000000, 2000000);
                p.nj.upyenMessage(num);
                p.sendYellowMessage("Bạn nhận được " + num + " yên");
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 599: {
                final ClanManager clanMng = p.nj.clan.clanManager();
                final ClanThanThu thanThu = clanMng.getCurrentThanThu();
                if (thanThu != null) {
                    if (thanThu.upExp(2)) {
                        p.nj.removeItemBag(index, 1);
                    }
                } else {
                    p.sendYellowMessage("Có cái nịt");
                }
                break;
            }
            case 600: {
                ClanManager clanMng = null;
                if (p.nj.clan != null) {
                    clanMng = p.nj.clan.clanManager();
                }
                ClanThanThu thanThu = null;
                if (clanMng != null) {
                    thanThu = clanMng.getCurrentThanThu();
                }
                if (thanThu != null && thanThu.upExp(5)) {
                    p.nj.removeItemBag(index, 1);
                } else {
                    p.sendYellowMessage("Có cái nịt");
                }
                break;
            }
            case 605: {
                ClanManager clanMng = null;
                if (p.nj.clan != null) {
                    clanMng = p.nj.clan.clanManager();
                }
                ClanThanThu thanThu = null;
                if (clanMng != null) {
                    thanThu = clanMng.getCurrentThanThu();
                }
                ClanThanThu.EvolveStatus result = null;
                if (thanThu != null) {
                    result = thanThu.evolve();
                }
                if (result == null) return;

                Message m = null;
                switch (result) {
                    case SUCCESS:
                        m = clanMng.createMessage("Gia tộc bạn nhận được " + clanMng.getCurrentThanThu().getPetItem().getData().name);
                        p.nj.removeItemBag(index, 1);
                        break;
                    case FAIL:
                        m = clanMng.createMessage("Tiến hoá thất bại bạn mất 1 tiến hoá đan");
                        p.nj.removeItemBag(index, 1);
                        break;
                    case MAX_LEVEL:
                        m = clanMng.createMessage("Thần thú của bạn đã đạt cấp cao nhất");
                        break;
                    case NOT_ENOUGH_STARS:
                        m = clanMng.createMessage("Thần thú của bạn không đủ sao để nâng cấp");
                        break;
                    default:
                }
                clanMng.sendMessage(m);
                break;
            }
            case 281: {
                final ClanManager clan = ClanManager.getClanByName(p.nj.clan.clanName);
                if (clan == null || clan.getMem(p.nj.name) == null) {
                    p.sendYellowMessage("Cần có gia tộc để sử dụng");
                    return;
                }
                if (clan.use_card <= 0) {
                    p.sendYellowMessage("Số lần sử dụng lệnh bài đã hết");
                    return;
                }
                clan.openDun += 1;
                clan.use_card -= 1;
                p.sendYellowMessage("Số lần đi Lãnh địa gia tộc tăng lên là " + clan.openDun);
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 597: {
                // Sử dụng cần câu
                item.setLock(true);
                if (numbagnull == 0) {
                    p.sendYellowMessage("Hành trang không đủ ô trống để câu cá");
                    return;
                }

                if (p.nj.y == 456 && (p.nj.x >= 107 && p.nj.x <= 2701)) {
                    boolean coMoi = false;
                    for (Item item1 : p.nj.ItemBag) {
                        if (item1 != null && (item1.id == 602 || item1.id == 603)) {
                            p.nj.removeItemBags(item1.id, 1);
                            coMoi = true;
                            break;
                        }
                    }

                    if (coMoi) {
                        if (util.percent(70, 30)) {
                            val random = new int[]{599, 600}[util.nextInt(2)];
                            int quantity = util.nextInt(0, 5);
                            final Item item1 = ItemData.itemDefault(random);
                            item1.quantity = quantity;
                            p.nj.addItemBag(true, item1);
                            p.sendYellowMessage("Bạn nhận được " + quantity);
                        } else {
                            p.sendYellowMessage("Không câu được gì cả");
                        }
                    } else {
                        p.sendYellowMessage("Không có mồi câu để câu cá");
                    }
                } else {
                    p.sendYellowMessage("Hãy đi đến vùng nước ở làng chài để câu cá");
                }

                break;
            }
            case 676: {
                p.nj.vuixuan += 1;
                p.sendYellowMessage("Số lần đánh Hộp quà bí ẩn của bạn tăng lên " + p.nj.vuixuan);
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 695:
            case 696:
            case 697:
            case 698:
            case 699:
            case 700:
            case 701:
            case 702:
            case 703: {
                if (numbagnull == 0) {
                    p.sendYellowMessage("Hành trang đầy");
                    return;
                }
                upDaDanhVong(p, item);
                break;
            }
            case 845: {//exp km
                final int num = util.nextInt(200, 500);
                p.nj.expkm +=(num);
                p.sendYellowMessage("Bạn nhận được " + num + " kinh mạch");
                p.nj.removeItemBag(index, 1);
                break;
            }
            case 843:{// sổ tay
                p.typemenu = 4445;
                server.menu.doMenuArray(p, new String[]{"Thông tin kinh mạch","Exp kinh mạch đang có"});//thông tin hiền nhân});
                }
            break;
            case 844:{// sổ tay
                p.typemenu = 4444;
                server.menu.doMenuArray(p, new String[]{" Khai mở kinh mạch","Nâng kinh mạch cấp 2","Nâng kinh mạch cấp 3","Nâng kinh mạch cấp 4","Nâng kinh mạch cấp 5","Nâng kinh mạch cấp 6","Nâng kinh mạch cấp 7","Nâng kinh mạch cấp 8","Nâng kinh mạch cấp 9","Hướng dẫn"});//thông tin hiền nhân});
                }
                break;

            default: {
                if (useItem.server.manager.EVENT != 0 &&
                        item != null &&
                        EventItem.isEventItem(item.id)) {

                    if (numbagnull == 0) {
                        p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                        return;
                    }

                    EventItem[] entrys = EventItem.entrys;
                    EventItem entry = null;
                    for (int i = 0; i < entrys.length; i++) {
                        entry = entrys[i];

                        if (entry == null) continue;
                        if (entry.getOutput().getId() == item.id) {
                            break;
                        }
                    }

                    if (entry == null) {
                        p.sendYellowMessage("Sự kiện này đã kết thúc không còn sử dụng được vật phẩm này nữa");
                        return;
                    }

                    p.updateExp(entry.getOutput().getExp(), false);
                    if (util.nextInt(10) < 3) {
                        p.updateExp(2 * entry.getOutput().getExp(), false);
                    } else {
                        final short[] arId = entry.getOutput().getIdItems();
                        final short idI = arId[util.nextInt(arId.length)];
                        if (randomItem(p, item.isLock(), idI)) return;
                        p.nj.removeItemBag(index, 1);
                    }

                    return;
                }
                break;
            }
        }
        final Message m = new Message(11);
        m.writer().writeByte(index);
        m.writer().writeByte(p.nj.get().speed());
        m.writer().writeInt(p.nj.get().getMaxHP());
        m.writer().writeInt(p.nj.get().getMaxMP());
        m.writer().writeShort(p.nj.get().eff5buffHP());
        m.writer().writeShort(p.nj.get().eff5buffMP());
        m.writer().flush();
        p.sendMessage(m);
        m.cleanup();
        if (ItemData.isTypeMounts(item.id)) {
            if (p.nj.getPlace() != null) {
                for (final User user : p.nj.getPlace().getUsers()) {
                    p.nj.getPlace().sendMounts(p.nj.get(), user);
                }
            }
        }

//        if (item.id >= 795) {
//            p.sendInfo(false);
//        }
if ((item.id >= 795 && item.id <= 805) || (item.id >= 813 && item.id <= 817) || (item.id >= 825 && item.id <= 827) || (item.id >= 830 && item.id <= 832)) {
            final Message m1 = new Message(57);
            m1.writer().flush();
           p.conn.sendMessage(m);
            m1.cleanup();
            if (!p.c.isTrade) {
                Service.CharViewInfo(p, false);
            }
}
        TaskHandle.useItemUpdate(p.nj, item.id);

    }

    private static boolean randomItem(User p, boolean isLock, short itemId) {
        Item itemup = ItemData.itemDefault(itemId);
        if (itemup == null) return true;

        if (itemup.isPrecious()) {
            if (!util.percent(100, itemup.getPercentAppear())) {
                itemup = Item.defaultRandomItem();
            }

            if ((itemup.id == 385) && !util.percent(100, itemup.getPercentAppear())) {
                itemup = Item.defaultRandomItem();
            }


        }


        itemup.setLock(isLock);

        p.nj.addItemBag(true, itemup);
        return false;
    }

    private static void upDaDanhVong(User p, Item item) {
        if (item.quantity >= 10) {
            short count = (short) (item.quantity / 10);
            val itemUp = ItemData.itemDefault(item.id + 1);
            itemUp.quantity = count;
            p.nj.removeItemBags(item.id, count * 10);
            p.nj.addItemBag(true, itemUp);
        } else {
            p.sendYellowMessage("Cần 10 viên đá danh vọng để nâng cấp");
        }
    }

    public static void useItemChangeMap(final User p, final Message m) {
        try {
            final byte indexUI = m.reader().readByte();
            final byte indexMenu = m.reader().readByte();
            m.cleanup();
            final Item item = p.nj.ItemBag[indexUI];
            if (item != null && (item.id == 37 || item.id == 35)) {
                if (item.id != 37) {
                    p.nj.removeItemBag(indexUI);
                }
                if (indexMenu == 0 || indexMenu == 1 || indexMenu == 2) {
                    final Map ma = getMapid(Map.arrTruong[indexMenu]);
                    if (TaskHandle.isLockChangeMap2((short) ma.id, p.nj.getTaskId())) {
                        GameCanvas.startOKDlg(p.session, Text.get(0, 84));
                        return;
                    }
                    for (final Place area : ma.area) {
                        if (area.getNumplayers() < ma.template.maxplayers) {
                            p.nj.getPlace().leave(p);
                            area.EnterMap0(p.nj);
                            return;
                        }
                    }
                }
                if (indexMenu == 3 || indexMenu == 4 || indexMenu == 5 || indexMenu == 6 || indexMenu == 7 || indexMenu == 8 || indexMenu == 9) {
                    final Map ma = getMapid(Map.arrLang[indexMenu - 3]);
                    assert ma != null;
                    if (TaskHandle.isLockChangeMap2((short) ma.id, p.nj.getTaskId())) {
                        GameCanvas.startOKDlg(p.session, Text.get(0, 84));
                        return;
                    }
                    for (final Place area : ma.area) {
                        if (area.getNumplayers() < ma.template.maxplayers) {
                            p.nj.getPlace().leave(p);
                            area.EnterMap0(p.nj);
                            return;
                        }
                    }
                }
            }
        } catch (IOException ex) {
        }
        p.nj.get().upDie();
    }


}
