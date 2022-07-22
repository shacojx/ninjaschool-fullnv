package server;

import real.Ninja;
import real.ClanManager;
import real.PlayerManager;
import java.io.IOException;
import threading.Message;
import real.User;
import threading.Server;
import threading.Manager;

public class Draw
{
    private static final Server server;
    
    public static void Draw(final User p, final Message m) throws IOException {
        final short menuId = m.reader().readShort();
        final String str = m.reader().readUTF();
        m.cleanup();
        util.Debug("menuId " + menuId + " str " + str);
        byte b = -1;
        try {
            b = m.reader().readByte();
        }
        catch (IOException ex) {}
        m.cleanup();
        switch (menuId) {
            case 1: {
                if (p.nj.quantityItemyTotal(279) <= 0) {
                    break;
                }
                final Ninja c = PlayerManager.getInstance().getNinja(str);
                if (c.getPlace() != null && !c.getPlace().map.isLangCo() && c.getPlace().map.getXHD() == -1) {
                    p.nj.getPlace().leave(p);
                    p.nj.get().x = c.get().x;
                    p.nj.get().y = c.get().y;
                    c.getPlace().Enter(p);
                    return;
                }
                p.sendYellowMessage("Ví trí người này không thể đi tới");
                break;
            }
            case 41_0:
                        p.nameUS = str;
                        Ninja n = PlayerManager.getInstance().getNinja(str);
                        if (n != null) {
                            server.menu.sendWrite(p, (short)41_0_0, "ID vật phẩm :");
                        } else {
                            p.sendYellowMessage("Người chơi không tồn tại hoặc không online");
                        }
                        break;
                    case 41_0_0:
                        p.idItemGF = str;
                        if (p.idItemGF != null) {
                            server.menu.sendWrite(p, (short)41_0_1, "Nhập số lượng :");
                        } else {
                            p.sendYellowMessage("Nhập sai");
                        }
                        break;
                    case 41_0_1:
                        p.itemQuantityGF = str;
                        p.sendItem1();
                        break;
                    case 41_1:
                        p.nameUS = str;
                        Ninja u = PlayerManager.getInstance().getNinja(str);
                        if (u != null) {
                            server.menu.sendWrite(p, (short)41_1_0, "ID vật phẩm :");
                        } else {
                            p.sendYellowMessage("Người chơi không tồn tại hoặc không online");
                        }
                        break;
                    case 41_1_0:
                        p.idItemGF = str;
                        if (p.idItemGF != null) {
                            server.menu.sendWrite(p, (short)41_1_1, "Nhập số lượng :");
                        } else {
                            p.sendYellowMessage("Nhập sai");
                        }
                        break;
                    case 41_1_1:
                        p.itemQuantityGF = str;
                        if (p.idItemGF != null) {
                            server.menu.sendWrite(p, (short)41_1_2, "Nhập cấp độ cho trang bị :");
                        } else {
                            p.sendYellowMessage("Nhập sai");
                        }
                        break;
                    case 41_1_2:
                        p.itemUpgradeGF = str;
                        if (p.idItemGF != null) {
                            server.menu.sendWrite(p, (short)41_1_3, "Nhập hệ trang bị:");
                        } else {
                            p.sendYellowMessage("Nhập sai");
                        }
                        break;
                    case 41_1_3:
                        p.itemSysGF = str;
                        p.sendTB();
                        break;
            case 49:
                p.giftcode = str;
                p.giftcode();
                break;
            case 50: {
                ClanManager.createClan(p, str);
                break;
            }
            case 51: {
                p.passnew = "";
                p.passold = str;
                p.changePassword();
                Draw.server.menu.sendWrite(p, (short)52, "Nhập mật khẩu mới");
                break;
            }
            case 52: {
                p.passnew = str;
                p.changePassword();
                break;
            }
            case 55:
                p.nameUS = str;
                Ninja a = PlayerManager.getInstance().getNinja(str);
                if (a != null) {
                    server.menu.sendWrite(p, (short) 56, "Nhập ID vật phẩm:");
                } else {
                    p.sendYellowMessage("Nhân vật này không tồn tại hoặc không online.");
                }
                break;
            case 56:
                p.idItemGF = str;
                server.menu.sendWrite(p, (short) 57, "Nhập số lượng vật phẩm:");
                break;
            case 57:
                p.itemQuantityGF = str;
                p.sendItem();
                break;
            case 58:
                p.nameUS = str;
                Ninja a1 = PlayerManager.getInstance().getNinja(str);
                if (a1 != null) {
                    server.menu.sendWrite(p, (short) 59, "Nhập lượng:");
                } else {
                    p.sendYellowMessage("Nhân vật này không tồn tại hoặc không online.");
                }
                break;
            case 59:
                p.luongGF = str;
                p.sendLuong();
                break;
            case 60:
                p.nameUS = str;
                Ninja a2 = PlayerManager.getInstance().getNinja(str);
                if (a2 != null) {
                    server.menu.sendWrite(p, (short) 61, "Nhập xu:");
                } else {
                    p.sendYellowMessage("Nhân vật này không tồn tại hoặc không online.");
                }
                break;
            case 61:
                p.xuGF = str;
                p.sendXu();
                break;
            case 62:
                p.nameUS = str;
                Ninja a3 = PlayerManager.getInstance().getNinja(str);
                if (a3 != null) {
                    server.menu.sendWrite(p, (short) 63, "Nhập yên:");
                } else {
                    p.sendYellowMessage("Nhân vật này không tồn tại hoặc không online.");
                }
                break;
            case 63:
                p.yenGF = str;
                p.sendYen();
                break;
            case 64:
                p.nameUS = str;
                Ninja a4 = PlayerManager.getInstance().getNinja(str);
                if (a4 != null) {
                    server.menu.sendWrite(p, (short) 65, "Nhập lời nhắn:");
                } else {
                    p.sendYellowMessage("Nhân vật này không tồn tại hoặc không online.");
                }
                break;
            case 65:
                p.messGF = str;
                p.sendMess();
                break;
            case 100: {
                if (b == 1) {
                    p.session.sendMessageLog("Chức năng tạm bảo trì");
                    return;
                }
                final String num = str.replaceAll(" ", "").trim();
                if (num.length() > 10 || !util.checkNumInt(num) || b < 0 || b >= Draw.server.manager.rotationluck.length) {
                    return;
                }
                final int xujoin = Integer.parseInt(num);
                Draw.server.manager.rotationluck[b].joinLuck(p, xujoin);
                break;
            }
            case 101: {
                if (b < 0 || b >= Draw.server.manager.rotationluck.length) {
                    return;
                }
                Draw.server.manager.rotationluck[b].luckMessage(p);
                break;
            }
            case 102: {
                p.typemenu = 92;
                MenuController.doMenuArray(p, new String[] { "Vòng xoay vip", "Vòng xoay thường" });
                break;
            }
            case 9991: {
                Manager.chatKTG(str);
                break;
            }
            case 9992: {
                if(!util.checkNumInt(str) || str.equals("")) {
                    p.session.sendMessageLog("Giá trị nhập vào không hợp lệ");
                    return;
                }
                String check = str.replaceAll(" ", "").trim();
                int num = Integer.parseInt(check);
                p.nj.get().setSpoint(p.nj.getSpoint() + num);
                p.loadSkill();
                break;
            }
            case 9993: {
                if(!util.checkNumInt(str) || str.equals("")) {
                    p.session.sendMessageLog("Giá trị nhập vào không hợp lệ");
                    return;
                }
                String check = str.replaceAll(" ", "").trim();
                int num = Integer.parseInt(check);
                p.nj.get().updatePpoint(p.nj.get().getPpoint() + num);
                p.updatePotential();
                break;
            }
            case 9994: {
                if(!util.checkNumInt(str) || str.equals("")) {
                    p.session.sendMessageLog("Giá trị nhập vào không hợp lệ");
                    return;
                }
                String check = str.replaceAll(" ", "").trim();
                int num = Integer.parseInt(check);
                p.nj.setLevel(p.nj.getLevel()+num);
                break;
            }
            case 9995: {
                if(!util.checkNumInt(str) || str.equals("")) {
                    p.session.sendMessageLog("Giá trị nhập vào không hợp lệ");
                    return;
                }
                String check = str.replaceAll(" ", "").trim();
                int num = Integer.parseInt(check);
                p.nj.upyenMessage(num);
                break;
            }
            case 9996: {
                if(!util.checkNumInt(str) || str.equals("")) {
                    p.session.sendMessageLog("Giá trị nhập vào không hợp lệ");
                    return;
                }
                String check = str.replaceAll(" ", "").trim();
                int num = Integer.parseInt(check);
                p.upluongMessage(num);
                break;
            }
            case 9997: {
                if(!util.checkNumInt(str) || str.equals("")) {
                    p.session.sendMessageLog("Giá trị nhập vào không hợp lệ");
                    return;
                }
                String check = str.replaceAll(" ", "").trim();
                int num = Integer.parseInt(check);
                p.nj.upxuMessage(num);
                break;
            }
            case 9998: {
                try {
                    if(!util.checkNumInt(str) || str.equals("")) {
                        p.session.sendMessageLog("Giá trị nhập vào không hợp lệ");
                        return;
                    }
                    String check = str.replaceAll(" ", "").trim();
                    int minues = Integer.parseInt(check);
                    if( minues < 0 || minues > 10) {
                        p.session.sendMessageLog("Giá trị nhập vào từ 0 -> 10 phút");
                        return;
                    }
                    p.sendYellowMessage("Đã kích hoạt bảo trì Server sau " + minues + " phút.");
                    for (int i = 0; i < minues; i++) {
                        Manager.serverChat("Thông báo", "Máy chủ sẽ tiến hành bảo trì sau " + (minues - i) + " phút nữa. Vui lòng thoát game để tránh mất dữ liệu.");
                        Thread.sleep(60000);
                    }
                    PlayerManager.getInstance().Clear();
                    server.stop();
                    break;
                }
                catch (InterruptedException ex) {}
            }
        }
    }
    
    static {
        server = Server.getInstance();
    }
}
