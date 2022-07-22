package server;

import boardGame.Place;
import lombok.SneakyThrows;
import lombok.val;
import patch.*;
import patch.clan.ClanTerritory;
import patch.clan.ClanTerritoryData;
import patch.interfaces.IBattle;
import patch.tournament.GeninTournament;
import patch.tournament.KageTournament;
import patch.tournament.Tournament;
import patch.tournament.TournamentData;
import real.*;
import tasks.TaskHandle;
import tasks.TaskList;
import tasks.Text;
import threading.Manager;
import threading.Map;
import threading.Message;
import threading.Server;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static patch.Constants.TOC_TRUONG;
import static patch.ItemShinwaManager.*;
import static patch.TaskOrder.*;
import static patch.tournament.Tournament.*;
import static real.User.TypeTBLOption.*;

public class MenuController {

    public static final String MSG_HANH_TRANG = "Hành trang ko đủ chổ trống";

    public static final int MIN_YEN_NVHN = 50;
    public static final int MAX_YEN_NVHN = 60;

    Server server;

    public MenuController() {
        this.server = Server.getInstance();
    }

    public void sendMenu(final User p, final Message m) throws IOException {
        final byte npcId = m.reader().readByte();
        byte menuId = m.reader().readByte();
        final byte optionId = m.reader().readByte();


        val ninja = p.nj;

        if (TaskHandle.isTaskNPC(ninja, npcId) && Map.isNPCNear(ninja, npcId)) {
            // TODO SELECT MENU TASK
            menuId = (byte) (menuId - 1);
            if (ninja.getTaskIndex() == -1) {

                if (menuId == -1) {
                    TaskHandle.Task(ninja, (short) npcId);
                    return;
                }
            } else if (TaskHandle.isFinishTask(ninja)) {
                if (menuId == -1) {
                    TaskHandle.finishTask(ninja, (short) npcId);
                    return;
                }
            } else if (ninja.getTaskId() == 1) {
                if (menuId == -1) {
                    TaskHandle.doTask(ninja, (short) npcId, menuId, optionId);
                    return;
                }
            } else if (ninja.getTaskId() == 7) {
                if (menuId == -1) {
                    TaskHandle.doTask(ninja, (short) npcId, menuId, optionId);
                    return;
                }
            } else if (ninja.getTaskId() == 8 || ninja.getTaskId() == 0) {
                boolean npcTalking = TaskHandle.npcTalk(ninja, menuId, npcId);
                if (npcTalking) {
                    return;
                }

            } else if (ninja.getTaskId() == 13) {
                if (menuId == -1) {
                    if (ninja.getTaskIndex() == 1) {
                        // OOka
                        final Map map = Server.getMapById(56);
                        val place = map.getFreeArea();
                        val npc = Ninja.getNinja("Thầy Ookamesama");
                        npc.p = new User();
                        npc.p.nj = npc;
                        npc.isNpc = true;
                        npc.setTypepk(Constants.PK_DOSAT);
                        p.nj.enterSamePlace(place, npc);
                        return;
                    } else if (ninja.getTaskIndex() == 2) {
                        // Haru
                        final Map map = Server.getMapById(0);
                        val place = map.getFreeArea();
                        val npc = Ninja.getNinja("Thầy Kazeto");
                        if (npc == null) {
                            System.out.println("KO THẦY ĐỐ MÀY LÀM NÊN");
                            return;
                        }
                        npc.p = new User();
                        npc.isNpc = true;
                        npc.p.nj = npc;
                        npc.setTypepk(Constants.PK_DOSAT);
                        p.nj.enterSamePlace(place, npc);
                        return;
                    } else if (ninja.getTaskIndex() == 3) {
                        final Map map = Server.getMapById(73);

                        val npc = Ninja.getNinja("Cô Toyotomi");
                        if (npc == null) {
                            System.out.println("KO THẦY ĐỐ MÀY LÀM NÊN");
                            return;
                        }
                        npc.isNpc = true;
                        npc.p = new User();
                        npc.setTypepk(Constants.PK_DOSAT);
                        npc.p.nj = npc;
                        val place = map.getFreeArea();
                        p.nj.enterSamePlace(place, npc);
                        return;
                    }
                } else if (ninja.getTaskId() == 15 &&
                        ninja.getTaskIndex() >= 1) {
                    if (menuId == -1) {
                        // Nhiem vu giao thu
                        if (ninja.getTaskIndex() == 1 && npcId == 14) {
                            p.nj.removeItemBags(214, 1);
                        } else if (ninja.getTaskIndex() == 2 && npcId == 15) {
                            p.nj.removeItemBags(214, 1);
                        } else if (ninja.getTaskIndex() == 3 && npcId == 16) {
                            p.nj.removeItemBags(214, 1);
                        }
                    }

                }
            }
        }

        m.cleanup();
        Label_6355:
        {
            label:
            switch (p.typemenu) {
                case 0: {
                    if (menuId == 0) {
                        // Mua vu khi
                        p.openUI(2);
                        break;
                    }
                    switch (menuId) {
                        case 1:
                            if (optionId == 0) {
                                // Thanh lap gia toc
                                if (!p.nj.clan.clanName.isEmpty()) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hiện tại con đã có gia tộc không thể thành lập thêm được nữa.");
                                    break label;
                                }
                                if (p.luong < ClanManager.LUONG_CREATE_CLAN) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Để thành lập gia tộc con cần phải có đủ 70.000 lượng trong người.");
                                    break label;
                                }
                                this.sendWrite(p, (short) 50, "Tên gia tộc");
                            } else if (optionId == 1) {
                                // Lanh địa gia tộc
                                if (p.getClanTerritoryData() == null) {
                                    if (p.nj.clan.typeclan == TOC_TRUONG) {

                                        if (p.nj.getAvailableBag() == 0) {
                                            p.sendYellowMessage("Hành trang không đủ để nhận chìa khoá");
                                            return;
                                        }
                                        val clan = ClanManager.getClanByName(p.nj.clan.clanName);
                                        if (clan.openDun <= 0) {
                                            p.sendYellowMessage("Số lần đi lãnh địa gia tộc đã hết vui lòng dùng thẻ bài hoặc đợi vào tuần");
                                            return;
                                        }

                                        val clanTerritory = new ClanTerritory(clan);
                                        Server.clanTerritoryManager.addClanTerritory(clanTerritory);
                                        p.setClanTerritoryData(new ClanTerritoryData(clanTerritory, p.nj));
                                        Server.clanTerritoryManager.addClanTerritoryData(p.getClanTerritoryData());

                                        clanTerritory.clanManager.openDun--;
                                        if (clanTerritory == null) {
                                            p.sendYellowMessage("Có lỗi xẩy ra");
                                            return;
                                        }
                                        val area = clanTerritory.getEntrance();
                                        if (area != null) {
                                            val item = ItemData.itemDefault(260);
                                            p.nj.addItemBag(false, item);
                                            if (p.getClanTerritoryData().getClanTerritory() != null) {

                                                if (p.getClanTerritoryData().getClanTerritory() != null) {
                                                    p.getClanTerritoryData().getClanTerritory().enterEntrance(p.nj);
                                                }

                                                clanTerritory.clanManager.informAll("Tộc trưởng đã mở lãnh địa gia tộc");
                                            } else {
                                                p.sendYellowMessage("Null sml");
                                            }
                                        } else {
                                            p.nj.getPlace().chatNPC(p, (short) npcId, "Hiện tại lãnh địa gia tộc không còn khu trống");
                                        }

                                    } else {
                                        p.sendYellowMessage("Chỉ những người ưu tú được tộc trưởng mời mới có thể vào lãnh địa gia tộc");
                                    }
                                } else {
                                    val data = p.getClanTerritoryData();
                                    if (data != null) {
                                        val teri = data.getClanTerritory();
                                        if (teri != null) teri.enterEntrance(p.nj);
                                    }
                                }

                            } else if (optionId == 2) {
                                if (p.nj.quantityItemyTotal(262) < 50) {
                                    p.sendYellowMessage("Hành trang của con không có đủ 50 đồng tiền gia tộc");
                                } else if (p.nj.getAvailableBag() == 0) {
                                    p.sendYellowMessage("Hành trang không đủ chỗ trống");
                                } else {
                                    Item it = ItemData.itemDefault(263);
                                    p.nj.addItemBag(true, it);
                                    p.nj.removeItemBags(262, 50);
                                }
                            }
                            break label;
                        case 2:
                            if (menuId != 2) {
                                break label;
                            }
                            if (p.nj.isNhanban) {
                                p.session.sendMessageLog("Chức năng này không dành cho phân thân");
                                return;
                            }
                            if (optionId == 0) {
                                Service.evaluateCave(p.nj);
                                break label;
                            }
                            Cave cave = null;
                            if (p.nj.caveID != -1) {
                                if (Cave.caves.containsKey(p.nj.caveID)) {
                                    cave = Cave.caves.get(p.nj.caveID);
                                    p.nj.getPlace().leave(p);
                                    cave.map[0].area[0].EnterMap0(p.nj);
                                }
                            } else if (p.nj.party != null && p.nj.party.cave == null && p.nj.party.master != p.nj.id) {
                                p.session.sendMessageLog("Chỉ có nhóm trưởng mới được phép mở cửa hang động");
                                return;
                            }
                            if (cave == null) {
                                if (p.nj.nCave <= 0) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Số lần vào hang động cảu con hôm nay đã hết hãy quay lại vào ngày mai.");
                                    return;
                                }
                                if (optionId == 1) {
                                    if (p.nj.getLevel() < 30 || p.nj.getLevel() > 39) {
                                        p.session.sendMessageLog("Trình độ không phù hợp");
                                        return;
                                    }
                                    if (p.nj.party != null) {
                                        synchronized (p.nj.party.ninjas) {
                                            for (byte i = 0; i < p.nj.party.ninjas.size(); ++i) {
                                                if (p.nj.party.ninjas.get(i).getLevel() < 30 || p.nj.party.ninjas.get(i).getLevel() > 39) {
                                                    p.session.sendMessageLog("Thành viên trong nhóm trình độ không phù hợp");
                                                    return;
                                                }
                                            }
                                        }
                                    }
                                    if (p.nj.party != null) {
                                        if (p.nj.party.cave == null) {
                                            cave = new Cave(3);
                                            p.nj.party.openCave(cave, p.nj.name);
                                        } else {
                                            cave = p.nj.party.cave;
                                        }
                                    } else {
                                        cave = new Cave(3);
                                    }
                                    p.nj.caveID = cave.caveID;
                                }
                                if (optionId == 2) {
                                    if (p.nj.getLevel() < 40 || p.nj.getLevel() > 49) {
                                        p.session.sendMessageLog("Trình độ không phù hợp");
                                        return;
                                    }
                                    if (p.nj.party != null) {
                                        synchronized (p.nj.party) {
                                            for (byte i = 0; i < p.nj.party.ninjas.size(); ++i) {
                                                if (p.nj.party.ninjas.get(i).getLevel() < 40 || p.nj.party.ninjas.get(i).getLevel() > 49) {
                                                    p.session.sendMessageLog("Thành viên trong nhóm trình độ không phù hợp");
                                                    return;
                                                }
                                            }
                                        }
                                    }
                                    if (p.nj.party != null) {
                                        if (p.nj.party.cave == null) {
                                            cave = new Cave(4);
                                            p.nj.party.openCave(cave, p.nj.name);
                                        } else {
                                            cave = p.nj.party.cave;
                                        }
                                    } else {
                                        cave = new Cave(4);
                                    }
                                    p.nj.caveID = cave.caveID;
                                }
                                if (optionId == 3) {
                                    if (p.nj.getLevel() < 50 || p.nj.getLevel() > 59) {
                                        p.session.sendMessageLog("Trình độ không phù hợp");
                                        return;
                                    }
                                    if (p.nj.party != null) {
                                        synchronized (p.nj.party.ninjas) {
                                            for (byte i = 0; i < p.nj.party.ninjas.size(); ++i) {
                                                if (p.nj.party.ninjas.get(i).getLevel() < 50 || p.nj.party.ninjas.get(i).getLevel() > 59) {
                                                    p.session.sendMessageLog("Thành viên trong nhóm trình độ không phù hợp");
                                                    return;
                                                }
                                            }
                                        }
                                    }
                                    if (p.nj.party != null) {
                                        if (p.nj.party.cave == null) {
                                            cave = new Cave(5);
                                            p.nj.party.openCave(cave, p.nj.name);
                                        } else {
                                            cave = p.nj.party.cave;
                                        }
                                    } else {
                                        cave = new Cave(5);
                                    }
                                    p.nj.caveID = cave.caveID;
                                }
                                if (optionId == 4) {
                                    if (p.nj.getLevel() < 60 || p.nj.getLevel() > 69) {
                                        p.session.sendMessageLog("Trình độ không phù hợp");
                                        return;
                                    }
                                    if (p.nj.party != null && p.nj.party.ninjas.size() > 1) {
                                        p.session.sendMessageLog("Hoạt động lần này chỉ được phép một mình");
                                        return;
                                    }
                                    cave = new Cave(6);
                                    p.nj.caveID = cave.caveID;
                                }
                                if (optionId == 5) {
                                    if (p.nj.getLevel() < 70 || p.nj.getLevel() > 89) {
                                        p.session.sendMessageLog("Trình độ không phù hợp");
                                        return;
                                    }
                                    if (p.nj.party != null) {
                                        synchronized (p.nj.party.ninjas) {
                                            for (byte i = 0; i < p.nj.party.ninjas.size(); ++i) {
                                                if (p.nj.party.ninjas.get(i).getLevel() < 70) {
                                                    p.session.sendMessageLog("Thành viên trong nhóm trình độ không phù hợp");
                                                    return;
                                                }
                                            }
                                        }
                                    }
                                    if (p.nj.party != null) {
                                        if (p.nj.party.cave == null) {
                                            cave = new Cave(7);
                                            p.nj.party.openCave(cave, p.nj.name);
                                        } else {
                                            cave = p.nj.party.cave;
                                        }
                                    } else {
                                        cave = new Cave(7);
                                    }
                                    p.nj.caveID = cave.caveID;
                                }
                                if (optionId == 6) {
                                    if (p.nj.getLevel() < 90 || p.nj.getLevel() > 150) {
                                        p.session.sendMessageLog("Trình độ không phù hợp");
                                        return;
                                    }

                                    if (p.nj.party != null && p.nj.party.getKey() != null &&
                                            p.nj.party.getKey().get().getLevel() >= 90) {
                                        synchronized (p.nj.party.ninjas) {
                                            for (byte i = 0; i < p.nj.party.ninjas.size(); ++i) {
                                                if (p.nj.party.ninjas.get(i).getLevel() < 90 || p.nj.party.ninjas.get(i).getLevel() > 151) {
                                                    p.session.sendMessageLog("Thành viên trong nhóm trình độ không phù hợp");
                                                    return;
                                                }
                                            }
                                        }
                                    }

                                    if (p.nj.party != null) {
                                        if (p.nj.party.cave == null) {
                                            cave = new Cave(9);
                                            p.nj.party.openCave(cave, p.nj.name);
                                        } else {
                                            cave = p.nj.party.cave;
                                        }
                                    } else {
                                        cave = new Cave(9);
                                    }
                                    p.nj.caveID = cave.caveID;
                                }
                                if (cave != null) {
                                    final Ninja c = p.nj;
                                    --c.nCave;
                                    p.nj.pointCave = 0;
                                    p.nj.getPlace().leave(p);
                                    cave.map[0].area[0].EnterMap0(p.nj);
                                }
                            }
                            p.setPointPB(p.nj.pointCave);
                            break label;
                        case 3: {
                            if (optionId == 0) {
                                // Thach dau loi dai
                                this.sendWrite(p, (short) 2, "Nhập tên đối thủ của ngươi vào đây");   
                                if ((p.nj.getTaskId() == 42 && p.nj.getTaskIndex() == 1)) {                                                
                                            p.nj.upMainTask();
                                }
                                break;
                            } else if (optionId == 1) {
                                // Xem thi dau
                                Service.sendBattleList(p);
                            }
                            }
                            break label;
                        case 4:
                            Random generator = new Random();
                            int value = generator.nextInt(3);
                            if (value == 0) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ở chỗ ta có rất nhiều binh khí có giá trị");
                            }
                            if (value == 1) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy chọn cho mình một món bình khí đi.");
                            }
                            if (value == 2) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Haha, nhà ngươi cần vũ khí gì?");
                            }
                            break label;
                    }
                }
                case 1: {
                    if (menuId == 0) {
                        if (optionId == 0) {
                            p.openUI(21 - p.nj.gender);
                            break;
                        }
                        if (optionId == 1) {
                            p.openUI(23 - p.nj.gender);
                            break;
                        }
                        if (optionId == 2) {
                            p.openUI(25 - p.nj.gender);
                            break;
                        }
                        if (optionId == 3) {
                            p.openUI(27 - p.nj.gender);
                            break;
                        }
                        if (optionId == 4) {
                            p.openUI(29 - p.nj.gender);
                            break;
                        }
                    } else if (menuId == 1) {
                        Random generator = new Random();
                        int value = generator.nextInt(3);
                        if (value == 0) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Giáp, giày giá rẻ đây!");
                        }
                        if (value == 1) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Không mặc giáp mua từ ta, ra khỏi trường ngươi sẽ gặp nguy hiểm.");
                        }
                        if (value == 2) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi cần giày, giáp sắt, quần áo?");
                        }
                        break label;
                    }
                    break;
                }
                case 2: {
                    if (menuId == 0) {
                        if (optionId == 0) {
                            p.openUI(16);
                            break;
                        } else if (optionId == 1) {
                            p.openUI(17);
                            break;
                        } else if (optionId == 2) {
                            p.openUI(18);
                            break;
                        } else if (optionId == 3) {
                            p.openUI(19);
                            break;
                        }
                    } else if (menuId == 1) {
                        if (optionId == 4) {
                            // Nang mat thuong
                            final val item = p.nj.get().ItemBody[14];
                            if (item != null && item.getUpgrade() != 0) {
                                nangMat(p, item, false);
                            } else {
                                p.sendYellowMessage("Hãy sử dụng Nguyệt Nhãn để sử dụng được chức năng này.");
                            }

                        } else if (optionId == 5) {
                            // Nang mắt vip
                            final val item = p.nj.get().ItemBody[14];
                            if (item != null && item.getUpgrade() != 0) {
                                nangMat(p, item, true);
                            } else {
                                p.sendYellowMessage("Hãy sử dụng Nguyệt Nhãn để sử dụng được chức năng này.");
                            }
                        } else if (optionId == 6) {
                            final List<int[]> data = MenuController.nangCapMat.keySet().stream()
                                    .map(s -> nangCapMat.get(s)).collect(Collectors.toList());

                            String s = "Sử dụng vật phẩm sự kiện để có thể nhận mắt 1\n";
                            for (int i = 0, dataSize = data.size(); i < dataSize; i++) {
                                int[] datum = data.get(i);
                                s += "-Nâng cấp mắt " + (i + 2) + " dùng " + datum[0] + " viên đá danh vọng cấp " + (i + 2) + " nâng thường " + datum[1] + " xu xác suất " + datum[2] + "%, VIP " + datum[1] + " xu " + datum[3] + " lượng xác suất " + datum[4] + "% \n\n";
                            }
                            Service.sendThongBao(p.nj, s);
                        }
                        break label;
                } else if (menuId == 2) {
                        Random generator = new Random();
                        int value = generator.nextInt(3);
                        if (value == 0) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con chọn loại trang sức gì nào?");
                        }
                        if (value == 1) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Trang sức không chỉ để ngắm, nó còn tăng sức mạnh của con");
                        }
                        if (value == 2) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con cần mua ngọc bội, nhẫn, dây chuyền, bùa họ thân à?");
                        }
                        break label;
                    }
                }
                break;
                case 3: {
                    if (menuId == 0) {
                        p.openUI(7);
                        break;
                    }
                    if (menuId == 1) {
                        p.openUI(6);
                        break;
                    }
                    if (menuId == 2) {
                        int num = util.nextInt(0, 1);

                        switch (num) {
                            case 0:
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Mua ngay HP,MP từ ta, được chế tạo từ loại thảo dược quý hiếm nhất");
                                break;
                            case 1:
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Đi đường cần mang theo ít dược phẩm");
                                break;
                        }
                    }
                }
                break;
                case 4: {
                    switch (menuId) {
                        case 0: {
                            p.openUI(9);
                            break;
                        }
                        case 1: {
                            p.openUI(8);
                            break;
                        }
                        case 2: {
                            int num = util.nextInt(0, 2);

                            switch (num) {
                                case 0:
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Ăn xong đảm bảo ngươi sẽ quay lại lần sau");
                                    break;
                                case 1:
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Thức ăn của ta là ngon nhất rồi");
                                    break;
                                case 2:
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hahaha, chắc ngươi đi đường cũng mệt rồi");
                                    break;
                            }
                        }
                        break;
                        case 3: {
                            switch (optionId) {
                                case 0: {
                                    // Đăng kí thien dia bang
                                    RegisterResult result = null;
                                    if (p.nj.get().getLevel() <= 80) {
                                        result = GeninTournament.gi().register(p);

                                    } else if (p.nj.get().getLevel() > 80 && p.nj.get().getLevel() <= 150) {
                                        result = KageTournament.gi().register(p);
                                    }

                                    if (result != null) {
                                        if (result == RegisterResult.SUCCESS) {
                                            p.nj.getPlace().chatNPC(p, (short) 4, "Bạn đã đăng kí thành công");
                                        } else if (result == RegisterResult.ALREADY_REGISTER) {
                                            p.nj.getPlace().chatNPC(p, (short) 4, "Bạn đã đăng kí thành công rồi");
                                        } else if (result == RegisterResult.LOSE) {
                                            p.nj.getPlace().chatNPC(p, (short) 4, "Bạn đã thua không thể đăng kí được");
                                        }
                                    } else {

                                    }
                                    break;
                                }
                                case 1: {
                                    //Chinh phuc thien dia bang
                                    try {
                                        final List<TournamentData> tournaments = getTypeTournament(p.nj.getLevel()).getChallenges(p);
                                        Service.sendChallenges(tournaments, p);
                                    } catch (Exception e) {

                                    }

                                    break;
                                }
                                case 2: {
                                    //Thien bang
                                    sendThongBaoTDB(p, KageTournament.gi(), "Thiên bảng\n");
                                    break;
                                }
                                case 3: {
                                    // Dia bang
                                    sendThongBaoTDB(p, GeninTournament.gi(), "Địa bảng\n");
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
                case 5: {
                    switch (menuId) {
                        case 0: {
                            p.openUI(4);
                            break;
                        }
                        case 1: {
                            p.nj.mapLTD = p.nj.getPlace().map.id;
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Lưu tọa độ thành công, khi kiệt sức con sẽ được khiêng về đây");
                            break;
                        }
                        case 2: {
                            if (optionId != 0) {
                                break;
                            }
                            // TODO Bo gioi up lv vdmq phan than
//                            if (p.nj.isNhanban) {
//                                p.conn.sendMessageLog("Chức năng này không dành cho phân thân");
//                                return;
//                            }

                            if (p.nj.getEffId(34) == null) {
                                p.nj.getPlace().chatNPC(p, (short) 5, "Phải dùng thí luyện thiếp mới có thể vào được");
                                return;
                            }
                            if (p.nj.getLevel() < 60) {
                                p.session.sendMessageLog("Chức năng yêu cầu trình độ 60");
                                return;
                            }
                            final Manager manager = this.server.manager;
                            final Map ma = Manager.getMapid(139);
                            for (final Place area : ma.area) {
                                if (area.getNumplayers() < ma.template.maxplayers) {
                                    p.nj.getPlace().leave(p);
                                    area.EnterMap0(p.nj);
                                    return;
                                }
                            }
                            break;
                        }
                        case 3: {
                            int num = util.nextInt(0, 2);

                            switch (num) {
                                case 0:
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Ta giữ đồ chưa hề để thất lạc bao giờ.");
                                    break;
                                case 1:
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy an tâm giao đồ cho ta nào!");
                                    break;
                                case 2:
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Trên người của ngươi toàn là đồ có giá trị, sao không cất bớt ở đây?");
                                    break;
                            }
                        }
                    }
                    break;
                }
                case 6: {
                    switch (menuId) {
                        case 0: {
                            if (optionId == 0) {
                                p.openUI(10);
                                break;
                            }
                            if (optionId == 1) {
                                p.openUI(31);
                                break;
                            }
                            break;
                        }
                        case 1: {
                            if (optionId == 0) {
                                p.openUI(12);
                                break;
                            }
                            if (optionId == 1) {
                                p.openUI(11);
                                break;
                            }
                            break;
                        }
                        case 2: {
                            p.openUI(13);
                            break;
                        }
                        case 3: {
                            p.openUI(33);
                            break;
                        }
                        case 4: {
                            // Luyen ngoc
                            p.openUI(46);
                            break;
                        }
                        case 5: {
                            // Kham ngoc
                            p.openUI(47);
                            break;
                        }
                        case 6: {
                            // Got ngoc
                            p.openUI(49);
                            break;
                        }
                        case 7: {
                            // Thao ngoc
                            p.openUI(50);
                            break;
                        }
                        case 8: {
                            int num = util.nextInt(0, 3);

                            switch (num) {
                                case 0:
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Ngươi muốn cải tiến trang bị?");
                                    break;
                                case 1:
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Nâng cấp trang bị:Uy tín, giá cả phải chăng.");
                                    break;
                                case 2:
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Đảm bảo sau khi nâng cấp đồ của ngươi sẽ tốt hơn hẳn");
                                    break;
                                case 3:
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Baggis ta đập đồ không bịp như sever khác đâu");
                                    break;
                            }
                        }
                    }
                    break;
                }
                case 7: {
                    if (menuId == 0) {
                        int num = util.nextInt(0, 2);

                        switch (num) {
                            case 0:
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Nhà ngươi muốn đi đâu?");
                                break;
                            case 1:
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Đi xe kéo của ta an toàn là số một.");
                                break;
                            case 2:
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ngựa của ta rất khỏe, có thể chạy ngàn dặm");
                                break;
                        }
                        break;
                    }
                    if (menuId > 0 && menuId <= Map.arrLang.length) {
                        final Map ma = Manager.getMapid(Map.arrLang[menuId - 1]);
                        for (final Place area : ma.area) {
                            if (area.getNumplayers() < ma.template.maxplayers) {
                                p.nj.getPlace().leave(p);
                                area.EnterMap0(p.nj);
                                return;
                            }
                        }
                        break;
                    }
                    break;
                }
                case 8: {
                    if (menuId == 3) {
                        int num = util.nextInt(0, 2);

                        switch (num) {
                            case 0:
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Nhà ngươi muốn đi đâu?");
                                break;
                            case 1:
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Đi xe kéo của ta an toàn là số một.");
                                break;
                            case 2:
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ngựa của ta rất khỏe, có thể chạy ngàn dặm");
                                break;
                        }
                        break;
                    }
                    if (menuId >= 0 && menuId < Map.arrTruong.length) {
                        final Map ma = Manager.getMapid(Map.arrTruong[menuId]);
                        for (final Place area : ma.area) {
                            if (area.getNumplayers() < ma.template.maxplayers) {
                                p.nj.getPlace().leave(p);
                                area.EnterMap0(p.nj);
                                return;
                            }
                        }
                        break;
                    }
                    break;
                }
                case 9: {
                    if (menuId == 0) {
                        if (optionId == 0) {
                            this.server.manager.sendTB(p, "Top đại gia yên", BXHManager.getStringBXH(0));
                        } else if (optionId == 1) {
                            this.server.manager.sendTB(p, "Top cao thủ", BXHManager.getStringBXH(1));
                        } else if (optionId == 2) {
                            this.server.manager.sendTB(p, "Top gia tộc", BXHManager.getStringBXH(2));
                        } else if (optionId == 3) {
                            this.server.manager.sendTB(p, "Top hang động", BXHManager.getStringBXH(3));
                        }
                    }
                    if (menuId == 1) {
                        if (p.nj.get().nclass > 0) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con đã vào lớp từ trước rồi mà");
                            break;
                        }
                        if (p.nj.get().ItemBody[1] != null) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con cần tháo vũ khí ra để đến đây nhập học nhé");
                            break;
                        }
                        if (p.nj.getAvailableBag() < 3) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang phải có đủ 2 ô để nhận đồ con nhé");
                            break;
                        }
//                        p.nj.addItemBag(false, ItemData.itemDefault(420));
                        if (optionId == 0) {
                            p.Admission((byte) 1);
                        } else if (optionId == 1) {
                            p.Admission((byte) 2);
                        }
                        p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy chăm chỉ quay tay để lên cấp con nhé");
                        break;
                    } else {
                        if (menuId != 2) {
                            break;
                        }
                        if (p.nj.get().nclass != 1 && p.nj.get().nclass != 2) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con không phải học sinh trường này nên không thể tẩy điểm ở đây");
                            break;
                        }
                        if (optionId == 0) {
                            p.restPpoint(p.nj.get());
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Ta đã giúp con tẩy điểm tiềm năng, hãy sử dụng tốt điểm tiềm năng nhé");
                            break;
                        }
                        if (optionId == 1) {
                            p.restSpoint();
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Ta đã giúp con tẩy điểm kĩ năng, hãy sử dụng tốt điểm kĩ năng nhé");
                            break;
                        }

                        break;
                    }
                }
                case 10: {
                    if (menuId == 0) {
                        if (optionId == 0) {
                            this.server.manager.sendTB(p, "Top đại gia yên", BXHManager.getStringBXH(0));
                        } else if (optionId == 1) {
                            this.server.manager.sendTB(p, "Top cao thủ", BXHManager.getStringBXH(1));
                        } else if (optionId == 2) {
                            this.server.manager.sendTB(p, "Top gia tộc", BXHManager.getStringBXH(2));
                        } else if (optionId == 3) {
                            this.server.manager.sendTB(p, "Top hang động", BXHManager.getStringBXH(3));
                        }
                    }
                    if (menuId == 1) {
                        if (p.nj.get().nclass > 0) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con đã vào lớp từ trước rồi mà");
                            break;
                        }
                        if (p.nj.get().ItemBody[1] != null) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con cần tháo vũ khí ra để đến đây nhập học nhé");
                            break;
                        }
                        if (p.nj.getAvailableBag() < 3) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang phải có đủ 2 ô để nhận đồ con nhé");
                            break;
                        }
//                        p.nj.addItemBag(false, ItemData.itemDefault(421));
                        if (optionId == 0) {
                            p.Admission((byte) 3);
                        } else if (optionId == 1) {
                            p.Admission((byte) 4);
                        }
                        p.nj.getPlace().chatNPC(p, (short) 9, "Hãy chăm chỉ quay tay để lên cấp con nhé");
                        break;
                    } else {
                        if (menuId != 2) {
                            break;
                        }
                        if (p.nj.get().nclass != 3 && p.nj.get().nclass != 4) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con không phải học sinh trường này nên không thể tẩy điểm ở đây");
                            break;
                        }
                        if (optionId == 0) {
                            p.restPpoint(p.nj.get());
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Ta đã giúp con tẩy điểm tiềm năng, hãy sử dụng tốt điểm tiềm năng nhé");
                            break;
                        }
                        if (optionId == 1) {
                            p.restSpoint();
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Ta đã giúp con tẩy điểm kĩ năng, hãy sử dụng tốt điểm kĩ năng nhé");
                            break;
                        }
                        break;
                    }
                }
                case 11: {
                    if (menuId == 0) {
                        if (optionId == 0) {
                            this.server.manager.sendTB(p, "Top đại gia yên", BXHManager.getStringBXH(0));
                        } else if (optionId == 1) {
                            this.server.manager.sendTB(p, "Top cao thủ", BXHManager.getStringBXH(1));
                        } else if (optionId == 2) {
                            this.server.manager.sendTB(p, "Top gia tộc", BXHManager.getStringBXH(2));
                        } else if (optionId == 3) {
                            this.server.manager.sendTB(p, "Top hang động", BXHManager.getStringBXH(3));
                        }
                    }
                    if (menuId == 1) {
                        if (p.nj.get().nclass > 0) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con đã vào lớp từ trước rồi mà");
                            break;
                        }
                        if (p.nj.get().ItemBody[1] != null) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con cần tháo vũ khí ra để đến đây nhập học nhé");
                            break;
                        }
                        if (p.nj.getAvailableBag() < 3) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang phải có đủ 2 ô để nhận đồ con nhé");
                            break;
                        }
//                        p.nj.addItemBag(false, ItemData.itemDefault(422));
                        if (optionId == 0) {
                            p.Admission((byte) 5);
                        } else if (optionId == 1) {
                            p.Admission((byte) 6);
                        }
                        p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy chăm chỉ quay tay để lên cấp con nhé");
                        break;
                    } else {
                        if (menuId != 2) {
                            break;
                        }
                        if (p.nj.get().nclass != 5 && p.nj.get().nclass != 6) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con không phải học sinh trường này nên không thể tẩy điểm ở đây");
                            break;
                        }
                        if (optionId == 0) {
                            p.restPpoint(p.nj.get());
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Ta đã giúp con tẩy điểm tiềm năng, hãy sử dụng tốt điểm tiềm năng nhé");
                            break;
                        }
                        if (optionId == 1) {
                            p.restSpoint();
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Ta đã giúp con tẩy điểm kĩ năng, hãy sử dụng tốt điểm kĩ năng nhé");
                            break;
                        }
                        break;
                    }
                }
                case 12: {
                    if (menuId == 0) {
                        break;
                    }
                    if (menuId == 3) {
                        //p.session.sendMessageLog("Tạm bảo trì phân thân");
                        if (p.nj.timeRemoveClone > System.currentTimeMillis()) {
                        p.toNhanBan();
                        break;
                        }
                        break;
                    } else {
                        if (menuId != 4) {
                            if (menuId == 2) {
                                p.nj.clearTask();
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Ta đã huỷ hết nhiệm vụ và vật phẩm nhiệm vụ của con lần sau làm nhiệm vụ tốt hơn nhé");
                                Service.getTask(p.nj);
                                break;
                            }
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con đang thực hiện nhiệm vụ kiên trì diệt ác, hãy chọn Menu/Nhiệm vụ để biết mình đang làm đến đâu");
                            break;
                        }
                        if (!p.nj.clone.isDie && p.nj.timeRemoveClone > System.currentTimeMillis()) {
                            p.exitNhanBan(false);
                            p.nj.clone.open(p.nj.timeRemoveClone, p.nj.getPramSkill(71));
                            break;
                        }
                        break;
                    }
                }

                case 14:
                case 15:
                case 16: {
                    boolean hasItem = false;
                    for (Item item : p.nj.ItemBag) {
                        if (item != null && item.id == 214) {
                            hasItem = true;
                            break;
                        }
                    }
                    if (hasItem) {
                        p.nj.removeItemBags(214, 1);
                        p.nj.getPlace().chatNPC(p, npcId, "Ta rất vui vì cô béo còn quan tâm đến ta.");
                        p.nj.upMainTask();
                    } else {
                        if (p.nj.getTaskId() == 20 && p.nj.getTaskIndex() == 1 && npcId == 15) {
                            p.nj.getPlace().leave(p);
                            final Map map = Server.getMapById(74);
                            val place = map.getFreeArea();
                            synchronized (place) {
                                p.expiredTime = System.currentTimeMillis() + 600000L;
                            }
                            Service.batDauTinhGio(p, 600);
                            place.refreshMobs();
                            place.EnterMap0(p.nj);
                        } else {
                            p.nj.getPlace().chatNPC(p, npcId, "Không có thư để con giao");
                        }
                    }
                    break;
                }
                case 17: {
                    val jaien = Ninja.getNinja("Jaian");
                    jaien.p = new User();
                    jaien.p.nj = jaien;
                    val place = p.nj.getPlace();
                    jaien.upHP(jaien.getMaxHP());
                    jaien.isDie = false;

                    jaien.x = place.map.template.npc[0].x;
                    jaien.id = -p.nj.id;
                    jaien.y = place.map.template.npc[0].y;
                    place.Enter(jaien.p);
                    Place.sendMapInfo(jaien.p, place);
                    break;
                }
                //NPC Cay thong
            case 31:
                switch (menuId) {
                case 0: {
                    if (p.nj.getAvailableBag() == 0) {
                        p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                        return;
                    } else if (p.nj.quantityItemyTotal(664) < 1) {
                        p.session.sendMessageLog("Bạn không đủ lồng đèn");
                        return;
                    } else {                        
                        short[] arId = new short[]{5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 8 , 8, 8, 8, 8, 8, 8 ,8, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9 ,9, 9, 9, 9, 9, 9, 9, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 275, 275, 275, 275, 276, 276, 276, 276, 277, 277, 277, 277, 278, 278, 278, 278, 275, 275, 276, 276, 277, 277, 278, 278, 275, 275, 276, 276, 277, 277, 278, 278, 275, 275, 276, 276, 277, 277, 278, 278, 340, 340, 383, 407, 408, 409, 410, 419, 436, 436, 436, 436, 436, 436, 437, 437, 437, 437, 437, 438, 438, 438, 568, 569, 570, 571, 577, 577, 575, 575, 695, 695, 695, 696, 696, 696, 449, 450, 451, 452, 453 ,337, 338, 567, 477, 477, 684, 684, 788, 788, 789, 789 , 778, 778, 778, 778, 778, 778, 778};
                        short idI = arId[util.nextInt(arId.length)];
                        Item itemup = ItemData.itemDefault(idI);
                        itemup.isLock = false;
                        //itemup.expires = util.TimeDay(7);
                        p.nj.addItemBag(true, itemup);
                        p.nj.topSK += 1;
                    }
                    p.nj.removeItemBags(664, 1);
                }
                break;
                case 1: {
                            this.server.manager.sendTB(p, "Hướng Dẫn", "1. Thả đèn cần 1 lồng đèn 1 lần lồng đèn mua tại npc vua gosho\n2. 1 lần trang trí sẽ được cộng 1 điểm\n3.Anh em đạt top thả đèn sẽ nhận được các phần quà hấp dẫn");
                            break;
                        }
                case 2: {
                            this.server.manager.sendTB(p, "Top thả đèn", BXHManager.getStringBXH(4));
                            break;
                        }
                case 3: {
                            this.server.manager.sendTB(p, "Top vui xuân", BXHManager.getStringBXH(5));
                            break;
                        }
                }
                break;
                case 35: {
                    switch (menuId) {
                        case 0: {
                            this.server.manager.sendTB(p, "Bảng Tin", "1. Sever sẽ open sự kiện tết nguyên đán");
                            break;
                        }
                        case 1: {
                            if (p.nj.quantityItemyTotal(484) < 5000) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ 5000 bít tất may mắn");
                                break;
                            } else if (p.nj.getAvailableBag() == 0) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
                            } else {
                                Item it = ItemData.itemDefault(836);
                                p.nj.addItemBag(true, it);
                                p.nj.removeItemBags(484, 5000);
                                break;
                            }
                        }
                        case 2: {
                            if (p.nj.quantityItemyTotal(582) < 7000) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ 7000 pháo hoa");
                                break;
                            } else if (p.nj.getAvailableBag() == 0) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
                            } else {
                                Item it = ItemData.itemDefault(846);
                                p.nj.addItemBag(true, it);
                                p.nj.removeItemBags(582, 7000);
                                break;                        
                            }    
                        }
                    }
                }
                break;
                case 37: {
                    switch (menuId) {
                        case 0: {
                            if (p.nj.quantityItemyTotal(646) < 1) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Cần có bùa may mắn");
                            break;
                        } else if (p.luong < 10000) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Cần 10000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hơi đen cho con, mong lần sau con sẽ may mắn hơn");
                                p.upluongMessage(-10000);
                                p.nj.removeItemBags(646, 1);
                                break;
                            } else {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Thành công, cùng xem chỉ số có ngon không nào");
                                final Item itemup = ItemData.itemDefault(397, (byte) util.nextInt(1, 3));  
                                itemup.isLock = true;
                                p.upluongMessage(-10000);
                                p.nj.removeItemBags(646, 1);
                                p.nj.addItemBag(false, itemup);
                                break;                        
                            }
                        }
                        }
                        case 1: {
                            if (p.nj.quantityItemyTotal(646) < 1) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Cần có bùa may mắn");
                            break;
                        } else if (p.luong < 10000) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Cần 10000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hơi đen cho con, mong lần sau con sẽ may mắn hơn");
                                p.upluongMessage(-10000);
                                p.nj.removeItemBags(646, 1);
                                break;
                            } else {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Thành công, cùng xem chỉ số có ngon không nào");
                                final Item itemup = ItemData.itemDefault(398, (byte) util.nextInt(1, 3)); 
                                itemup.isLock = true;
                                p.upluongMessage(-10000);
                                p.nj.removeItemBags(646, 1);
                                p.nj.addItemBag(false, itemup);
                                break;   
                            }
                        }
                        }
                        case 2: {
                            if (p.nj.quantityItemyTotal(646) < 1) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Cần có bùa may mắn");
                            break;
                        } else if (p.luong < 10000) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Cần 10000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hơi đen cho con, mong lần sau con sẽ may mắn hơn");
                                p.upluongMessage(-10000);
                                p.nj.removeItemBags(646, 1);
                                break;
                            } else {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Thành công, cùng xem chỉ số có ngon không nào");
                                final Item itemup = ItemData.itemDefault(399, (byte) util.nextInt(1, 3)); 
                                itemup.isLock = true;
                                p.upluongMessage(-10000);
                                p.nj.removeItemBags(646, 1);
                                p.nj.addItemBag(false, itemup);
                                break; 
                            }
                            }
                        }
                        case 3: {
                            if (p.nj.quantityItemyTotal(646) < 1) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Cần có bùa may mắn");
                            break;
                        } else if (p.luong < 10000) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Cần 10000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hơi đen cho con, mong lần sau con sẽ may mắn hơn");
                                p.upluongMessage(-10000);
                                p.nj.removeItemBags(646, 1);
                                break;
                            } else {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Thành công, cùng xem chỉ số có ngon không nào");
                                final Item itemup = ItemData.itemDefault(400, (byte) util.nextInt(1, 3)); 
                                itemup.isLock = true;
                                p.upluongMessage(-10000);
                                p.nj.removeItemBags(646, 1);
                                p.nj.addItemBag(false, itemup);
                                break;   
                            }
                        }
                        }
                        case 4: {
                            if (p.nj.quantityItemyTotal(646) < 1) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Cần có bùa may mắn");
                            break;
                        } else if (p.luong < 10000) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Cần 10000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hơi đen cho con, mong lần sau con sẽ may mắn hơn");
                                p.upluongMessage(-10000);
                                p.nj.removeItemBags(646, 1);
                                break;
                            } else {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Thành công, cùng xem chỉ số có ngon không nào");
                                final Item itemup = ItemData.itemDefault(401, (byte) util.nextInt(1, 3));
                                itemup.isLock = true;
                                p.upluongMessage(-10000);
                                p.nj.removeItemBags(646, 1);
                                p.nj.addItemBag(false, itemup);
                                break;   
                            }
                        }
                        }
                        case 5: {
                            if (p.nj.quantityItemyTotal(646) < 1) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Cần có bùa may mắn");
                            break;
                        } else if (p.luong < 10000) {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Cần 10000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hơi đen cho con, mong lần sau con sẽ may mắn hơn");
                                p.upluongMessage(-10000);
                                p.nj.removeItemBags(646, 1);
                                break;
                            } else {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Thành công, cùng xem chỉ số có ngon không nào");
                                final Item itemup = ItemData.itemDefault(402, (byte) util.nextInt(1, 3)); 
                                itemup.isLock = true;
                                p.upluongMessage(-10000);
                                p.nj.removeItemBags(646, 1);
                                p.nj.addItemBag(false, itemup);
                                break;   
                            }
                        }
                        }
                        case 6: {
                            this.server.manager.sendTB(p, "Hướng Dẫn", "1. Chức năng luyện bí kíp \n2. Anh em luyện bí kíp cần có Bùa may mắn(mua tại npc gosho) + 10k lượng 1 lần luyện-khi luyện sẽ có tỉ lể thành công và thất bại \n3. Khi thất bại các bạn sẽ mất bùa may mắn + 10k lượng \n4. Khi thành công các bạn sẽ nhận được radom 1 trong 3 chỉ số của bí kíp đó nếu đen thì nhận được chỉ số kui-nếu may mắn sẽ nhận được chỉ số ngon \n5. Lưu ý: cái thuộc tính ở bí kíp không ảnh hưởng gì nhé anh em");
                            break;                                               
                        }
                    }
                }            
                break;      
                case 18: {
                    int num = util.nextInt(0, 2);

                    switch (num) {
                        case 0:
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Làng ta sống chủ yếu là nghề biển");
                            break;
                        case 1:
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Sống ở làng Chài thì con cần học cách đánh bắt cá.");
                            break;
                        case 2:
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Đây là làng Chài, do ta quản lý.");
                            break;
                    }
                }
                break;
                case 19: {
                    int num = util.nextInt(0, 2);

                    switch (num) {
                        case 0:
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Làng ta khí hậu ôn hòa cây cối quanh năm tươi tốt");
                            break;
                        case 1:
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Dân làng sống ra hòa thuận, mọi người rất yêu hòa bình.");
                            break;
                        case 2:
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Ta là Kirin, ngôi làng này do ta cai quản.");
                            break;
                        }
                    }
                break;
                case 21: {
                    int num = util.nextInt(0, 2);

                    switch (num) {
                        case 0:
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Khí hậu làng ta rất lạnh, sống ở đây phải chăm chỉ rèn luyện");
                            break;
                        case 1:
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Con thích săn bắt không? Ta rất thích đi săn bắt");
                            break;
                        case 2:
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Ta là Sunoo, ngôi làng này do ta cai quản.");
                            break;
                    }
                }
                break;
                case 22: {
                    switch (menuId) {
                        case 0: {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Chức Năng Ninja Đệ Nhất sẽ cố gắng ra mắt sớm nhất");
                            break;
                        }
                    }
                }
                break;
                case 36: {
                    switch (menuId) {
                        case 0: {
                            if (p.nj.exptype == 0) {
                                p.nj.exptype = 1;
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Đã tắt không nhận kinh nghiệm");
                                break;
                            }
                            p.nj.exptype = 0;
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Đã bật không nhận kinh nghiệm");
                            break;
                        }
                        case 1: {
                            p.passold = "";
                            this.sendWrite(p, (short) 51, "Nhập mật khẩu cũ");
                            break;
                        }
                        case 2: {
                            if (p.nj.ddClan) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hôm nay con đã điểm danh rồi nhé, hãy quay lại đây vào ngày mai");
                                break;
                            }
                            p.nj.ddClan = true;
                            final ClanManager clan = ClanManager.getClanByName(p.nj.clan.clanName);
                            p.upluongMessage((long) (1000));
                            p.nj.upyenMessage((long) (50000000));
                            //p.nj.upxuMessage((long) (1000000));
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Điểm danh mỗi ngày sẽ nhận được các phần quà giá trị");
                            break;
                        }
                        case 3: {
                            if (p.nj.quantityItemyTotal(788) < 15000) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ 15000 nham thạch");
                                break;
                            } else if (p.nj.getAvailableBag() == 0) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
                            } else {
                                Item it = ItemData.itemDefault(786);
                                p.nj.addItemBag(true, it);
                                p.nj.removeItemBags(788, 15000);
                                break;
                            }
                        }
                        case 4: {
                            if (p.nj.quantityItemyTotal(789) < 15000) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ 15000 pha lê");
                                break;
                            } else if (p.nj.getAvailableBag() == 0) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
                            } else {
                                Item it = ItemData.itemDefault(787);
                                p.nj.addItemBag(true, it);
                                p.nj.removeItemBags(789, 15000);
                                break;
                            }
                        }
                        case 5: {
                            if (p.nj.quantityItemyTotal(682) < 35000) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ 35000 đá mặt trăng");
                                break;
                            } else if (p.nj.getAvailableBag() == 0) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
                            } else {
                                Item it = ItemData.itemDefault(797);
                                p.nj.addItemBag(true, it);
                                p.nj.removeItemBags(682, 35000);
                                break;
                            }
                        }
                    }
                }
                break;
                case 25: {
                    switch (menuId) {
                        case 0: {
                            switch (optionId) {
                                case 0: {
                                    // Nhiem vu hang ngay
                                    if (p.nj.getTasks()[NHIEM_VU_HANG_NGAY] == null && p.nj.nvhnCount < 20) {
                                        val task = createTask(p.nj.getLevel());
                                        if (task != null) {
                                            p.nj.addTaskOrder(task);
                                        } else {
                                            p.nj.getPlace().chatNPC(p, (short) 25, "Nhiệm vụ lần này có chút trục trặc chắc con không làm được rồi ahihi");
                                        }
                                    } else if (p.nj.nvhnCount >= 20) {
                                        p.nj.getPlace().chatNPC(p, (short) 25, "Nhiệm vụ hôm nay con đã làm hết quay lại vào ngày hôm sau");
                                    } else {
                                        p.nj.getPlace().chatNPC(p, (short) 25, "Nhiệm vụ lần trước ta giao cho con vẫn chưa hoàn thành");
                                    }
                                    break;
                                }
                                case 1: {
                                    // Huy nhiem vu
                                    p.nj.huyNhiemVu(NHIEM_VU_HANG_NGAY);
                                    break;
                                }
                                case 2: {
                                    // Hoan thanh
                                    if (!p.nj.hoanThanhNhiemVu(NHIEM_VU_HANG_NGAY)) {
                                        p.nj.getPlace().chatNPC(p, (short) 25, "Hãy hoàn thành nhiệm vụ để được nhận thưởng");
                                    } else {
                                        // TODO nhan qua NVHN
                                        p.upluongMessage(util.nextInt(MIN_YEN_NVHN, MAX_YEN_NVHN));
                                        p.nj.upyenMessage(util.nextInt(MIN_YEN_NVHN * 50, MAX_YEN_NVHN * 100));
                                        if ((p.nj.getTaskId() == 30 && p.nj.getTaskIndex() == 1) ||
                                                (p.nj.getTaskId() == 39 && p.nj.getTaskIndex() == 3)) {
                                            p.nj.upMainTask();
                                        }
                                    }
                                    break;
                                }

                                case 3: {
                                    // Di toi
                                    if (p.nj.getTasks() != null &&
                                            p.nj.getTasks()[NHIEM_VU_HANG_NGAY] != null
                                    ) {
                                        val task = p.nj.getTasks()[NHIEM_VU_HANG_NGAY];
                                        val map = Server.getMapById(task.getMapId());
                                        p.nj.setMapid(map.id);
                                        for (Npc npc : map.template.npc) {
                                            if (npc.id == 13) {
                                                p.nj.x = npc.x;
                                                p.nj.y = npc.y;
                                                p.nj.getPlace().leave(p);
                                                map.getFreeArea().Enter(p);
                                                break;
                                            }
                                        }
                                        p.nj.getPlace().chatNPC(p, (short) 25, "Nhiệm vụ lần này gặp lỗi con hãy đi up level lên đi rồi nhận lại nhiệm vụ từ ta");
                                    } else {
                                        p.nj.getPlace().chatNPC(p, (short) 25, "Hãy nhận nhiệm vụ từ ta để có thể chuyển map");
                                    }
                                }
                            }
                            break;
                        }
                        case 1: {
                            // Ta thu
                            switch (optionId) {
                                case 0: {
                                    //Nhan nhiem vu
                                    if (p.nj.getTasks()[NHIEM_VU_TA_THU] == null) {
                                        if (p.nj.taThuCount > 0) {
                                            val task = createBeastTask(p.nj.getLevel());
                                            if (task != null) {
                                                p.nj.addTaskOrder(task);
                                            } else {
                                                p.nj.getPlace().chatNPC(p, (short) 25, "Nhiệm vụ ngày hôm nay đã hêt");
                                            }
                                        } else {
                                            p.nj.getPlace().chatNPC(p, (short) 25, "Nhiệm vụ ngày hôm nay đã hêt");
                                        }
                                    } else {
                                        p.nj.getPlace().chatNPC(p, (short) 25, "Nhiệm vụ lần trước ta giao cho con vẫn chưa hoàn thành");
                                    }
                                    break;
                                }
                                case 1: {
                                    p.nj.huyNhiemVu(NHIEM_VU_TA_THU);
                                    break;
                                }
                                case 2: {
                                    if (!p.nj.hoanThanhNhiemVu(NHIEM_VU_TA_THU)) {
                                        p.nj.getPlace().chatNPC(p, (short) 25, "Hãy hoàn thành nhiệm vụ để được nhận thưởng");
                                    } else {
                                        val i = ItemData.itemDefault(251);
                                        i.quantity = p.nj.get().getLevel() >= 60 ? 5 : 2;
                                        p.nj.addItemBag(true, i);
                                        if ((p.nj.getTaskId() == 30 && p.nj.getTaskIndex() == 2) || (p.nj.getTaskId() == 39 && p.nj.getTaskIndex() == 1)) {
                                            //p.upluongMessage(util.nextInt(MIN_YEN_NVHN * 10, MAX_YEN_NVHN * 10));
                                            p.nj.upyenMessage(util.nextInt(MIN_YEN_NVHN * 80, MAX_YEN_NVHN * 150));
                                            p.nj.upMainTask();
                                        }
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                        case 2: {
                            // Chien truong
                            switch (optionId) {
                                case 0: {
                                    // bach
                                    p.nj.enterChienTruong(IBattle.CAN_CU_DIA_BACH);
                                    break;
                                }
                                case 1: {
                                    // hac gia
                                    p.nj.enterChienTruong(IBattle.CAN_CU_DIA_HAC);
                                    break;
                                }
                                case 2: {
                                    Service.sendBattleResult(p.nj, Server.getInstance().globalBattle);
                                    break;
                                }
                                case 3: {
                                    this.server.manager.sendTB(p, "Hướng Dẫn", "1. Chiến trường sẽ mở vào các khung giờ 13h 16h và 21h. \n2.Anh em nhớ điểm danh mới có thể vào nhé");   
                                }
                            }                            
                            break;
                        }
                    }
                    break;
                }
                case 26: {
                    if (menuId == 0) {
                        p.openUI(14);
                        break;
                    }
                    if (menuId == 1) {
                        p.openUI(15);
                        break;
                    }
                    if (menuId == 2) {
                        p.openUI(32);
                        break;
                    }
                    if (menuId == 3) {
                        p.openUI(34);
                        break;
                    }
                    break;
                }
                case 30: {
                    switch (menuId) {
                        case 0: {
                            p.openUI(38);
                            break;
                        }
                        case 1:
                            this.sendWrite(p, (short) 49, "Mã quà tặng:");
                            break;
                        case 2: {
                            if (optionId == 0) {
                                this.server.manager.rotationluck[0].luckMessage(p);
                                break;
                            }
                            if (optionId == 2) {
                                this.server.manager.sendTB(p, "Vòng xoay vip", "Tham gia đi xem luật lm gì");
                                break;
                            }
                            break;
                        }
                        case 3: {
                            if (optionId == 0) {
                                this.server.manager.rotationluck[1].luckMessage(p);
                                break;
                            }
                            if (optionId == 2) {
                                this.server.manager.sendTB(p, "Vòng xoay thường", "Tham gia đi xem luật lm gì");
                                break;
                            }
                            break;
                        }
                    }
                    break;
                }
                case 32: {
                    switch (menuId) {
                        case 0: {
                            switch (optionId) {
                                case 0: {
                                    // Chien truong keo Tham gia
                                    Server.candyBattleManager.enter(p.nj);
                                    break;
                                }
                                case 1: {
                                    // Chien truong keo huong dan
                                    Service.sendThongBao(p.nj, "Chiến trường kẹo:\n" +
                                            "\t- 20 ninja sẽ chia làm 2 đội Kẹo Trăng và Kẹo Đen.\n" +
                                            "\t- Mỗi đội chơi sẽ có nhiệm vụ tấn công giở kẹo của đối phương, nhặt kẹo và sau đó chạy về bỏ vào giỏ kẹo của bên đội mình.\n" +
                                            "\t- Trong khoảng thời gian ninja giữ kẹo sẽ bị mất một lượng HP nhất định theo thời gian.\n" +
                                            "\t- Giữ càng nhiều thì nguy hiểm càng lớn.\n" +
                                            "\t- Còn 10 phú cuối cùng sẽ xuất hiện Phù Thuỷ");
                                    break;
                                }
                            }
                            break;
                        }
                        case 1: {
                            // Option 1
                            val clanManager = ClanManager.getClanByName(p.nj.clan.clanName);
                            if (clanManager != null) {
                                // Có gia tọc và khong battle
                                if (clanManager.getClanBattle() == null) {
                                    //  Chua duoc moi battle
                                    if (p.nj.getClanBattle() == null) {
                                        // La toc truong thach dau
                                        if (p.nj.clan.typeclan == TOC_TRUONG) {
                                            if (clanManager.getClanBattleData() == null ||
                                                    (clanManager.getClanBattleData() != null && clanManager.getClanBattleData().isExpired())) {
                                                sendWrite(p, (byte) 4, "Nhập vào gia tộc muốn chiến đấu");
                                            } else {
                                                if (clanManager.restore()) {
                                                    enterClanBattle(p, clanManager);
                                                } else {
                                                    p.nj.getPlace().chatNPC(p, (short) 32, "Không hỗ trợ");
                                                }
                                            }
                                        } else {
                                            // Thử tìm battle data
                                            p.nj.getPlace().chatNPC(p, (short) 32, "Không hỗ trợ");
                                        }
                                    }
                                } else {
                                    enterClanBattle(p, clanManager);
                                }
                            }
                            break;
                        }
                        case 4: {
                            if (optionId == 0) {
                                p.openUI(43);
                            } else if (optionId == 1) {
                                p.openUI(44);
                                break;
                            } else if (optionId == 2) {
                                p.openUI(45);
                                break;
                            }
                            break;
                        }
                    }
                    break;
                }
                case 33: {
                    if (p.typemenu != 33) {
                        break;
                    }
                    switch (this.server.manager.EVENT) {
                        case 1: {
                            switch (menuId) {
                                case 0: {
                                    if (p.nj.quantityItemyTotal(432) < 1 || p.nj.quantityItemyTotal(428) < 3 || p.nj.quantityItemyTotal(429) < 2 || p.nj.quantityItemyTotal(430) < 3) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ nguyên liệu");
                                        break;
                                    }
                                    if (p.nj.getAvailableBag() == 0) {
                                        p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                                        break;
                                    }
                                    final Item it = ItemData.itemDefault(434);
                                    p.nj.addItemBag(true, it);
                                    p.nj.removeItemBags(432, 1);
                                    p.nj.removeItemBags(428, 3);
                                    p.nj.removeItemBags(429, 2);
                                    p.nj.removeItemBags(430, 3);
                                    break;
                                }
                                case 1: {
                                    if (p.nj.quantityItemyTotal(433) < 1 || p.nj.quantityItemyTotal(428) < 2 || p.nj.quantityItemyTotal(429) < 3 || p.nj.quantityItemyTotal(431) < 2) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ nguyên liệu");
                                        break;
                                    }
                                    if (p.nj.getAvailableBag() == 0) {
                                        p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                                        break;
                                    }
                                    final Item it = ItemData.itemDefault(435);
                                    p.nj.addItemBag(true, it);
                                    p.nj.removeItemBags(433, 1);
                                    p.nj.removeItemBags(428, 2);
                                    p.nj.removeItemBags(429, 3);
                                    p.nj.removeItemBags(431, 2);
                                    break;
                                }
                            }
                            break Label_6355;
                        }
                        case 2: {
                            switch (menuId) {
                                case 0: {
                                    if (p.nj.quantityItemyTotal(304) < 1 || p.nj.quantityItemyTotal(298) < 1 || p.nj.quantityItemyTotal(299) < 1 || p.nj.quantityItemyTotal(300) < 1 || p.nj.quantityItemyTotal(301) < 1) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ nguyên liệu");
                                        break;
                                    }
                                    if (p.nj.getAvailableBag() == 0) {
                                        p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                                        break;
                                    }
                                    final Item it = ItemData.itemDefault(302);
                                    p.nj.addItemBag(true, it);
                                    p.nj.removeItemBags(304, 1);
                                    p.nj.removeItemBags(298, 1);
                                    p.nj.removeItemBags(299, 1);
                                    p.nj.removeItemBags(300, 1);
                                    p.nj.removeItemBags(301, 1);
                                    break;
                                }
                                case 1: {
                                    if (p.nj.quantityItemyTotal(305) < 1 || p.nj.quantityItemyTotal(298) < 1 || p.nj.quantityItemyTotal(299) < 1 || p.nj.quantityItemyTotal(300) < 1 || p.nj.quantityItemyTotal(301) < 1) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ nguyên liệu");
                                        break;
                                    }
                                    if (p.nj.getAvailableBag() == 0) {
                                        p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                                        break;
                                    }
                                    final Item it = ItemData.itemDefault(303);
                                    p.nj.addItemBag(true, it);
                                    p.nj.removeItemBags(305, 1);
                                    p.nj.removeItemBags(298, 1);
                                    p.nj.removeItemBags(299, 1);
                                    p.nj.removeItemBags(300, 1);
                                    p.nj.removeItemBags(301, 1);
                                    break;
                                }
                                case 2: {
                                    if (p.nj.yen < 10000 || p.nj.quantityItemyTotal(292) < 3 || p.nj.quantityItemyTotal(293) < 2 || p.nj.quantityItemyTotal(294) < 3) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ nguyên liệu hoặc yên");
                                        break;
                                    }
                                    if (p.nj.getAvailableBag() == 0) {
                                        p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                                        break;
                                    }
                                    final Item it = ItemData.itemDefault(298);
                                    p.nj.addItemBag(true, it);
                                    p.nj.upyenMessage(-10000L);
                                    p.nj.removeItemBags(292, 3);
                                    p.nj.removeItemBags(293, 2);
                                    p.nj.removeItemBags(294, 3);
                                    break;
                                }
                                case 3: {
                                    if (p.nj.yen < 10000 || p.nj.quantityItemyTotal(292) < 2 || p.nj.quantityItemyTotal(295) < 3 || p.nj.quantityItemyTotal(294) < 2) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ nguyên liệu hoặc yên");
                                        break;
                                    }
                                    if (p.nj.getAvailableBag() == 0) {
                                        p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                                        break;
                                    }
                                    final Item it = ItemData.itemDefault(299);
                                    p.nj.addItemBag(true, it);
                                    p.nj.upyenMessage(-10000L);
                                    p.nj.removeItemBags(292, 2);
                                    p.nj.removeItemBags(295, 3);
                                    p.nj.removeItemBags(294, 2);
                                    break;
                                }
                                case 4: {
                                    if (p.nj.yen < 10000 || p.nj.quantityItemyTotal(292) < 2 || p.nj.quantityItemyTotal(295) < 3 || p.nj.quantityItemyTotal(297) < 3) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ nguyên liệu hoặc yên");
                                        break;
                                    }
                                    if (p.nj.getAvailableBag() == 0) {
                                        p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                                        break;
                                    }
                                    final Item it = ItemData.itemDefault(300);
                                    p.nj.addItemBag(true, it);
                                    p.nj.upyenMessage(-10000L);
                                    p.nj.removeItemBags(292, 2);
                                    p.nj.removeItemBags(295, 3);
                                    p.nj.removeItemBags(297, 3);
                                    break;
                                }
                                case 5: {
                                    if (p.nj.yen < 10000 || p.nj.quantityItemyTotal(292) < 2 || p.nj.quantityItemyTotal(296) < 2 || p.nj.quantityItemyTotal(297) < 3) {
                                        p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang của con không có đủ nguyên liệu hoặc yên");
                                        break;
                                    }
                                    if (p.nj.getAvailableBag() == 0) {
                                        p.session.sendMessageLog("Hành trang không đủ chỗ trống");
                                        break;
                                    }
                                    final Item it = ItemData.itemDefault(301);
                                    p.nj.addItemBag(true, it);
                                    p.nj.upyenMessage(-10000L);
                                    p.nj.removeItemBags(292, 2);
                                    p.nj.removeItemBags(296, 2);
                                    p.nj.removeItemBags(297, 3);
                                    break;
                                }
                            }
                            break Label_6355;
                        }
                        default: {
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Hiện tại chưa có sự kiện diễn ra");
                            break Label_6355;
                        }
                    }
                }
                //cpanel
                case -125:
                    if (menuId == 0) { //Item
                        if (p.id != 1) {
                            p.nj.place.chatNPC(p, (short) npcId, "Bạn Không Có Quyền");
                            break;
                        } else {
                            this.sendWrite(p, (short) 55, "Nhập tên tài khoản:");
                            break;
                        }
                    } else if (menuId == 1) { //Xu
                        if (p.id != 1) {
                            p.nj.place.chatNPC(p, (short) npcId, "Bạn Không Có Quyền");
                            break;
                        } else {
                            this.sendWrite(p, (short) 60, "Nhập tên tài khoản:");
                            break;
                        }
                    } else if (menuId == 2) { //Lượng
                        if (p.id != 1) {
                            p.nj.place.chatNPC(p, (short) npcId, "Bạn Không Có Quyền");
                            break;
                        } else {
                            this.sendWrite(p, (short) 58, "Nhập tên tài khoản:");
                            break;
                        }
                    } else if (menuId == 3) { //yên
                        if (p.id != 1) {
                            p.nj.place.chatNPC(p, (short) npcId, "Bạn Không Có Quyền");
                            break;
                        } else {
                            this.sendWrite(p, (short) 62, "Nhập tên tài khoản:");
                            break;
                        }
                    } else if (menuId == 4) { //Mess
                        if (p.id != 1) {
                            p.nj.place.chatNPC(p, (short) npcId, "Bạn Không Có Quyền");
                            break;
                        } else {
                            this.sendWrite(p, (short) 64, "Nhập tên tài khoản:");
                            break;
                        }
                    }
                    break;
                case 92: {
                    p.typemenu = ((menuId == 0) ? 93 : 94);
                    this.doMenuArray(p, new String[]{"Thông tin", "Luật chơi"});
                    break;
                }
                case 93: {
                    if (menuId == 0) {
                        this.server.manager.rotationluck[0].luckMessage(p);
                        break;
                    }
                    if (menuId == 1) {
                        this.server.manager.sendTB(p, "Vòng xoay vip", "Tham gia đi xem luật lm gì");
                        break;
                    }
                    break;
                }
                case 94: {
                    if (menuId == 0) {
                        this.server.manager.rotationluck[1].luckMessage(p);
                        break;
                    }
                    if (menuId == 1) {
                        this.server.manager.sendTB(p, "Vòng xoay thường", "Tham gia đi xem luật lm gì");
                        break;
                    }
                    break;
                }
                case 95: {
                    break;
                }
                case 120: {
                    if (menuId > 0 && menuId < 7) {
                        p.Admission(menuId);
                        break;
                    }
                    break;
                }
                case 23: {
                    // Matsurugi
                    if (ninja.getTaskId() == 23 && ninja.getTaskIndex() == 1 && menuId == 0) {
                        boolean hasItem = false;
                        for (Item item : p.nj.ItemBag) {
                            if (item != null && item.id == 230) {
                                hasItem = true;
                                break;
                            }
                        }

                        if (!hasItem) {
                            val i = ItemData.itemDefault(230);
                            i.setLock(true);
                            p.nj.addItemBag(false, i);
                            p.nj.getPlace().chatNPC(p, 23, "Ta hi vọng đây là lần cuối ta giao chìa khoá cho con ta nghĩ lần này con sẽ làm được. ");
                        } else {
                            p.nj.getPlace().chatNPC(p, 23, "Con đã có chìa khoá rồi không thể nhận thêm được");
                        }
                    } else {
                        p.nj.getPlace().chatNPC(p, 23, "Ta không quen biết con con đi ra đi");
                    }
                    break;
                }
                case 20: {
                    // Soba
                    if (menuId == 0) {
                        if (!ninja.hasItemInBag(266)) {
                            if (ninja.getTaskId() == 32 && ninja.getTaskIndex() == 1) {
                                val item = ItemData.itemDefault(266);
                                item.setLock(true);
                                ninja.addItemBag(false, item);
                            }
                        } else {
                            ninja.p.sendYellowMessage("Con đã có cần câu không thể nhận thêm");
                        }
                    } else {
                        ninja.getPlace().chatNPC(ninja.p, 20, "Làng ta rất thanh bình con có muốn sống ở đây không");
                    }
                    break;
                }
                case 28: {
                    // Shinwa
                    switch (menuId) {
                        case 0: {
                            final List<ItemShinwa> itemShinwas = items.get((int) optionId);
                            Message mess = new Message(103);
                            mess.writer().writeByte(optionId);
                            if (itemShinwas != null) {
                                mess.writer().writeInt(itemShinwas.size());
                                for (ItemShinwa item : itemShinwas) {
                                    val itemStands = item.getItemStand();
                                    mess.writer().writeInt(itemStands.getItemId());
                                    mess.writer().writeInt(itemStands.getTimeEnd());
                                    mess.writer().writeShort(itemStands.getQuantity());
                                    mess.writer().writeUTF(itemStands.getSeller());
                                    mess.writer().writeInt(itemStands.getPrice());
                                    mess.writer().writeShort(itemStands.getItemTemplate());
                                }
                            } else {
                                mess.writer().writeInt(0);
                            }
                            mess.writer().flush();
                            p.sendMessage(mess);
                            mess.cleanup();
                            break;
                        }
                        case 1: {
                            // Sell item
                            p.openUI(36);
                            break;
                        }
                        case 2: {
                            // Get item back

                            for (ItemShinwa itemShinwa : items.get(-2)) {
                                if (p.nj.getAvailableBag() == 0) {
                                    p.sendYellowMessage("Hành trang không đủ ô trống để nhận thêm");
                                    break;
                                }
                                if (itemShinwa != null) {
                                    if (p.nj.name.equals(itemShinwa.getSeller())) {
                                        itemShinwa.item.quantity = itemShinwa.getQuantity();
                                        p.nj.addItemBag(true, itemShinwa.item);
                                        items.get(-2).remove(itemShinwa);
                                        deleteItem(itemShinwa);
                                    }
                                }
                            }

                            break;
                        }
                    }
                    break;
                }
                case 27: {
                    // Cam chia khoa co quan
                    if (Arrays.stream(p.nj.ItemBag).anyMatch(item -> item != null && (item.id == 231 || item.id == 260))) {
                        p.nj.removeItemBags(231, 1);
                        p.nj.removeItemBags(260, 1);
                        p.getClanTerritoryData().getClanTerritory().plugKey(p.nj.getMapid(), p.nj);

                    } else {
                        p.sendYellowMessage("Không có chìa khoá để cắm");
                    }
                    break;
                }

                //Menu npc Okenachan
                case 24:
                    switch (menuId) {
                        case 0:
                            if (optionId == 0) {
                                if (p.luong <= 50) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Cần 50 lượng để đổi 250k xu");

                                    return;
                                } else {
                                    p.nj.upxuMessage(250000);
                                    p.upluongMessage(-50L);
                                    return;
                                }
                            }
                            if (optionId == 1) {
                                if (p.luong <= 50) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Cần 50 lượng để đổi 550k yên");

                                    return;
                                } else {
                                    p.nj.upyenMessage(550000);
                                    p.upluongMessage(-50L);
                                    return;
                                }
                            }
                        case 1:
                            if (p.nj.yen <= 1000000) {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Đừng lừa tao, mày có đủ xu éo đâu ?");

                                return;
                            } else {
                                p.nj.getPlace().chatNPC(p, (short) npcId, "Cần mua xu thì LH Admin nhé ");
                                return;
                            }
                        case 2:
                            p.nj.getPlace().chatNPC(p, (short) npcId, "Nạp lượng thì LH Admin nhé");
                            return;
                        case 3:
                            if (optionId == 0) {
                                if (p.nj.getLevel() < 10) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Trình độ của con không đủ để nhận thưởng.");
                                } else if (p.nj.quacap10 == 2) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Con đã nhận phần thưởng này rồi. Mỗi người chỉ được nhận 1 lần.");
                                } else if (p.nj.getAvailableBag() < 1) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
                                } else {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy luyện tập chăm chỉ để tăng cấp và nhận phần thưởng con nhé");
                                    p.nj.quacap10 = 2;
                                    p.nj.upyenMessage(10000000);
                                    //p.nj.upxuMessage(1000000);
                                    p.upluongMessage(1000);
                                }

                            }
                            //level 20
                            if (optionId == 1) {
                                if (p.nj.getLevel() < 20) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Trình độ của con không đủ để nhận thưởng.");
                                } else if (p.nj.quacap20 == 2) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Con đã nhận phần thưởng này rồi. Mỗi người chỉ được nhận 1 lần.");
                                } else if (p.nj.getAvailableBag() < 1) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
                                } else {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy luyện tập chăm chỉ để tăng cấp và nhận phần thưởng con nhé");
                                    p.nj.quacap20 = 2;
                                    p.nj.upyenMessage(20000000);
                                    //p.nj.upxuMessage(2000000);
                                    p.upluongMessage(2000);
                                }

                            }
                            //level 30
                            if (optionId == 2) {
                                if (p.nj.getLevel() < 30) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Trình độ của con không đủ để nhận thưởng.");
                                } else if (p.nj.quacap30 == 2) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Con đã nhận phần thưởng này rồi. Mỗi người chỉ được nhận 1 lần.");
                                } else if (p.nj.getAvailableBag() < 1) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
                                } else {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy luyện tập chăm chỉ để tăng cấp và nhận phần thưởng con nhé");
                                    p.nj.quacap30 = 2;
                                    p.nj.upyenMessage(30000000);
                                    //p.nj.upxuMessage(3000000);
                                    p.upluongMessage(3000);
                                }

                            }
                            //level 40
                            if (optionId == 3) {
                                if (p.nj.getLevel() < 40) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Trình độ của con không đủ để nhận thưởng.");
                                } else if (p.nj.quacap40 == 2) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Con đã nhận phần thưởng này rồi. Mỗi người chỉ được nhận 1 lần.");
                                } else if (p.nj.getAvailableBag() < 1) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
                                } else {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy luyện tập chăm chỉ để tăng cấp và nhận phần thưởng con nhé");
                                    p.nj.quacap40 = 2;
                                    p.nj.upyenMessage(40000000);
                                    //p.nj.upxuMessage(4000000);
                                    p.upluongMessage(4000);
                                }

                            }
                            //level 50
                            if (optionId == 4) {
                                if (p.nj.getLevel() < 50) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Trình độ của con không đủ để nhận thưởng.");
                                } else if (p.nj.quacap50 == 2) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Con đã nhận phần thưởng này rồi. Mỗi người chỉ được nhận 1 lần.");
                                } else if (p.nj.getAvailableBag() < 1) {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hành trang không đủ chỗ trống");
                                } else {
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Hãy luyện tập chăm chỉ để tăng cấp và nhận phần thưởng con nhé");
                                    p.nj.quacap50 = 2;
                                    p.nj.upyenMessage(50000000);
                                    //p.nj.upxuMessage(5000000);
                                    p.upluongMessage(5000);

                                    Item it = new Item();
                                    it.id = 383;
                                    it.quantity = 1;
                                    it.isLock = true;
                                    p.nj.addItemBag(true, it);
                                }

                            }
                            return;
                        case 4:
                            this.sendWrite(p, (short) 49, "Mã quà tặng:");
                            break;
                        case 5: {
                            int num = util.nextInt(0, 2);

                            switch (num) {
                                case 0:
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Con hãy chăm đánh quái, làm nhiệm vụ để có nhiều yên hơn");
                                    break;
                                case 1:
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Ta là hiện thân của thần tài sẽ mang tài lộc đến cho mọi người");
                                    break;
                                case 2:
                                    p.nj.getPlace().chatNPC(p, (short) npcId, "Online mỗi ngày để tham gia các hoạt động để tích lũy điểm hoạt động con nhé");
                                    break;
                            }
                        }
                    }
                    break;
                case 572: {
                    switch (menuId) {
                        case 0: {
                            p.typeTBLOption = $240;
                            break;
                        }
                        case 1: {
                            p.typeTBLOption = $480;
                            break;
                        }
                        case 2: {
                            p.typeTBLOption = ALL_MAP;
                            break;
                        }
                        case 3: {
                            p.typeTBLOption = PICK_ALL;
                            break;
                        }
                        case 4: {
                            p.typeTBLOption = USEFUL;
                            break;
                        }
                        case 5: {
                            p.activeTBL = !p.activeTBL;
                        }
                    }
                    break;
                }
                case 849: // Hoa Tuyết
                    switch (menuId) {
                        case 0: {
                            if (p.nj.quantityItemyTotal(775) >= 3000) {
                                p.nj.removeItemBags(775, 3000);
                                p.sendYellowMessage("Bạn nhận được Santa Claus");

                                Item it = ItemData.itemDefault(774);
                                p.nj.addItemBag(true, it);
                                return;
                            } else if (p.nj.quantityItemyTotal(775) < 3000) {
                                p.session.sendMessageLog("Bạn chưa đủ 3000 hoa tuyết để đổi");
                                return;
                            }
                        }
                    }
                    break;
                case 4444: {
                if (menuId == 0) { //luyện chiêu hiền nhân
                    if (p.nj.lvkm !=0){
                    p.session.sendMessageLog("con đã học chiêu này rồi");
                    return;
                    }
                    if (p.nj.expkm < 5000000){
                    p.session.sendMessageLog("Không đủ 5 triệu EXP kinh mạch để nâng, hãy đi đánh tinh anh thủ lĩnh boss rồi quay lại đây tao chỉ cho");
                    break;
                    } else if (p.luong < 10000) {
                    p.session.sendMessageLog("Chưa đủ lượng nhé con");                    
                    return;
                    } else{
                    byte pkoolvn = (byte) util.nextInt(1, 100);
                    if (pkoolvn <= 70) {
                         p.upluongMessage(-10000);
                         p.session.sendMessageLog("tư chất con còn kém lắm về luyện thêm đi rồi đến đây nhé, ta xin tiền học phí");
                         return;
                    }else{
                        p.upluongMessage(-10000);
                        p.nj.expkm -= 5000000;
                        p.nj.lvkm = 1;
                        p.session.sendMessageLog("con đã học thành công kinh mạch hiện tại đang là lv1");
                    }
                    }
                    break;
            }
                if (menuId == 1) { //luyện chiêu hiền nhân
                    if (p.nj.lvkm !=1){
                    p.session.sendMessageLog("Mở kinh mạch đi rồi đến gặp tao để nâng");
                    return;
                    }
                    if (p.nj.expkm < 10000000){
                    p.session.sendMessageLog("Không đủ 10 triệu EXP kinh mạch để nâng, hãy đi đánh tinh anh thủ lĩnh boss rồi quay lại đây tao chỉ cho");
                    break;
                    } else if (p.luong < 20000) {
                    p.session.sendMessageLog("Chưa đủ lượng nhé con");
                    return;
                    } else{
                    byte pkoolvn = (byte) util.nextInt(1, 100);
                    if (pkoolvn <= 70) {
                         p.upluongMessage(-20000);
                         p.session.sendMessageLog("tư chất con còn kém lắm về luyện thêm đi rồi đến đây nhé, ta xin tiền học phí");
                         return;
                    }else{
                        p.upluongMessage(-20000);
                        p.nj.expkm -= 10000000;
                        p.nj.lvkm = 2;
                        p.session.sendMessageLog("con đã nâng thành công kinh mạch hiện tại đang là lv2");
                    }
                    }
                    break;
            }
                if (menuId == 2) { //luyện chiêu hiền nhân
                    if (p.nj.lvkm !=2){
                    p.session.sendMessageLog("Nâng kinh mạch lên cấp 2 đi rồi đến gặp tao để nâng");
                    return;
                    }
                    if (p.nj.expkm < 15000000){
                    p.session.sendMessageLog("Không đủ 15 triệu EXP kinh mạch để nâng, hãy đi đánh tinh anh thủ lĩnh boss rồi quay lại đây tao chỉ cho");
                    break;
                    } else if (p.luong < 30000) {
                    p.session.sendMessageLog("Chưa đủ lượng nhé con");
                    return;
                    } else{
                    byte pkoolvn = (byte) util.nextInt(1, 100);
                    if (pkoolvn <= 70) {
                         p.upluongMessage(-30000);
                         p.session.sendMessageLog("tư chất con còn kém lắm về luyện thêm đi rồi đến đây nhé, ta xin tiền học phí");
                         return;
                    }else{
                        p.upluongMessage(-30000);
                        p.nj.expkm -= 15000000;
                        p.nj.lvkm = 3;
                        p.session.sendMessageLog("con đã nâng thành công kinh mạch hiện tại đang là lv3");
                    }
                    }
                    break;
            }
                if (menuId == 3) { //luyện chiêu hiền nhân
                    if (p.nj.lvkm !=3){
                    p.session.sendMessageLog("Nâng kinh mạch lên cấp 3 đi rồi đến gặp tao để nâng");
                    return;
                    }
                    if (p.nj.expkm < 20000000){
                    p.session.sendMessageLog("Không đủ 20 triệu EXP kinh mạch để nâng, hãy đi đánh tinh anh thủ lĩnh boss rồi quay lại đây tao chỉ cho");
                    break;
                    } else if (p.luong < 40000) {
                    p.session.sendMessageLog("Chưa đủ lượng nhé con");
                    return;
                    } else{
                    byte pkoolvn = (byte) util.nextInt(1, 100);
                    if (pkoolvn <= 70) {
                         p.upluongMessage(-40000);
                         p.session.sendMessageLog("tư chất con còn kém lắm về luyện thêm đi rồi đến đây nhé, ta xin tiền học phí");
                         return;
                    }else{
                        p.upluongMessage(-40000);
                        p.nj.expkm -= 20000000;
                        p.nj.lvkm = 4;
                        p.session.sendMessageLog("con đã nâng thành công kinh mạch hiện tại đang là lv4");
                    }
                    }
                    break;
            }
                if (menuId == 4) { //luyện chiêu hiền nhân
                    if (p.nj.lvkm !=4){
                    p.session.sendMessageLog("Nâng kinh mạch lên cấp 4 đi rồi đến gặp tao để nâng");
                    return;
                    }
                    if (p.nj.expkm < 25000000){
                    p.session.sendMessageLog("Không đủ 25 triệu EXP kinh mạch để nâng, hãy đi đánh tinh anh thủ lĩnh boss rồi quay lại đây tao chỉ cho");
                    break;
                    } else if (p.luong < 50000) {
                    p.session.sendMessageLog("Chưa đủ lượng nhé con");
                    return;
                    } else{
                    byte pkoolvn = (byte) util.nextInt(1, 100);
                    if (pkoolvn <= 70) {
                         p.upluongMessage(-50000);
                         p.session.sendMessageLog("tư chất con còn kém lắm về luyện thêm đi rồi đến đây nhé, ta xin tiền học phí");
                         return;
                    }else{
                        p.upluongMessage(-50000);
                        p.nj.expkm -= 25000000;
                        p.nj.lvkm = 5;
                        p.session.sendMessageLog("con đã nâng thành công kinh mạch hiện tại đang là lv5");
                    }
                    }
                    break;
            }
                if (menuId == 5) { //luyện chiêu hiền nhân
                    if (p.nj.lvkm !=5){
                    p.session.sendMessageLog("Nâng kinh mạch lên cấp 5 đi rồi đến gặp tao để nâng");
                    return;
                    }
                    if (p.nj.expkm < 30000000){
                    p.session.sendMessageLog("Không đủ 30 triệu EXP kinh mạch để nâng, hãy đi đánh tinh anh thủ lĩnh boss rồi quay lại đây tao chỉ cho");
                    break;
                    } else if (p.luong < 60000) {
                    p.session.sendMessageLog("Chưa đủ lượng nhé con");
                    return;
                    } else{
                    byte pkoolvn = (byte) util.nextInt(1, 100);
                    if (pkoolvn <= 70) {
                         p.upluongMessage(-60000);
                         p.session.sendMessageLog("tư chất con còn kém lắm về luyện thêm đi rồi đến đây nhé, ta xin tiền học phí");
                         return;
                    }else{
                        p.upluongMessage(-60000);
                        p.nj.expkm -= 30000000;
                        p.nj.lvkm = 6;
                        p.session.sendMessageLog("con đã nâng thành công kinh mạch hiện tại đang là lv6");
                    }
                    }
                    break;
            }
                if (menuId == 6) { //luyện chiêu hiền nhân
                    if (p.nj.lvkm !=6){
                    p.session.sendMessageLog("Nâng kinh mạch lên cấp 6 đi rồi đến gặp tao để nâng");
                    return;
                    }
                    if (p.nj.expkm < 35000000){
                    p.session.sendMessageLog("Không đủ 35 triệu EXP kinh mạch để nâng, hãy đi đánh tinh anh thủ lĩnh boss rồi quay lại đây tao chỉ cho");
                    break;
                    } else if (p.luong < 70000) {
                    p.session.sendMessageLog("Chưa đủ lượng nhé con");
                    return;
                    } else{
                    byte pkoolvn = (byte) util.nextInt(1, 100);
                    if (pkoolvn <= 70) {
                         p.upluongMessage(-70000);
                         p.session.sendMessageLog("tư chất con còn kém lắm về luyện thêm đi rồi đến đây nhé, ta xin tiền học phí");
                         return;
                    }else{
                        p.upluongMessage(-70000);
                        p.nj.expkm -= 35000000;
                        p.nj.lvkm = 7;
                        p.session.sendMessageLog("con đã nâng thành công kinh mạch hiện tại đang là lv7");
                    }
                    }
                    break;
            }
                if (menuId == 7) { //luyện chiêu hiền nhân
                    if (p.nj.lvkm !=7){
                    p.session.sendMessageLog("Nâng kinh mạch lên cấp 7 đi rồi đến gặp tao để nâng");
                    return;
                    }
                    if (p.nj.expkm < 40000000){
                    p.session.sendMessageLog("Không đủ 40 triệu EXP kinh mạch để nâng, hãy đi đánh tinh anh thủ lĩnh boss rồi quay lại đây tao chỉ cho");
                    break;
                    } else if (p.luong < 80000) {
                    p.session.sendMessageLog("Chưa đủ lượng nhé con");
                    return;
                    } else{
                    byte pkoolvn = (byte) util.nextInt(1, 100);
                    if (pkoolvn <= 70) {
                         p.upluongMessage(-80000);
                         p.session.sendMessageLog("tư chất con còn kém lắm về luyện thêm đi rồi đến đây nhé, ta xin tiền học phí");
                         return;
                    }else{
                        p.upluongMessage(-80000);
                        p.nj.expkm -= 40000000;
                        p.nj.lvkm = 8;
                        p.session.sendMessageLog("con đã nâng thành công kinh mạch hiện tại đang là lv8");
                    }
                    }
                    break;
            }
                if (menuId == 8) { //luyện chiêu hiền nhân
                    if (p.nj.lvkm !=8){
                    p.session.sendMessageLog("Nâng kinh mạch lên cấp 8 đi rồi đến gặp tao để nâng");
                    return;
                    }
                    if (p.nj.expkm < 50000000){
                    p.session.sendMessageLog("Không đủ 50 triệu EXP kinh mạch để nâng, hãy đi đánh tinh anh thủ lĩnh boss rồi quay lại đây tao chỉ cho");
                    break;
                    } else if (p.luong < 100000) {
                    p.session.sendMessageLog("Chưa đủ lượng nhé con");
                    return;
                    } else{
                    byte pkoolvn = (byte) util.nextInt(1, 100);
                    if (pkoolvn <= 70) {
                         p.upluongMessage(-100000);
                         p.session.sendMessageLog("tư chất con còn kém lắm về luyện thêm đi rồi đến đây nhé, ta xin tiền học phí");
                         return;
                    }else{
                        p.upluongMessage(-100000);
                        p.nj.expkm -= 50000000;
                        p.nj.lvkm = 9;
                        p.session.sendMessageLog("con đã nâng thành công kinh mạch hiện tại đang là lv9");
                    }
                    }
                    break;
            }
                
                if (menuId == 9) {
                    server.manager.sendTB(p, "Điều Kiện học kinh mạch", "Exp kinh mạch nhận được thông qua việc đánh tinh anh, thủ lĩnh"
                            + "\n>Kinh mạch<"
                            + "\n-Con cần  5 triệu exp Kinh mạch và 10k lượng để có thể học"
                            + "\n-lv2 cần 10 triệu exp Kinh mạch và 20k lượng"
                            + "\n-lv3 cần 15 triệu exp Kinh mạch và 30k lượng"
                            + "\n-lv4 cần 20 triệu exp Kinh mạch và 40k lượng"
                            + "\n-lv5 cần 25 triệu exp Kinh mạch và 50k lượng"
                            + "\n-lv6 cần 30 triệu exp Kinh mạch và 60k lượng"
                            + "\n-lv7 cần 35 triệu exp Kinh mạch và 70k lượng"
                            + "\n-lv8 cần 40 triệu exp Kinh mạch và 80k lượng"
                            + "\n-lv9 cần 50 triệu exp Kinh mạch và 100k lượng"
                            + "\n-thành công Kinh mạch sẽ lên lv và nhận đc hiệu ứng tương ứng"
                            + "\n-thất bại sẽ mất lượng exp giữ nguyên"
                            );
                    return;
                }
            }
            break;
                    case 4445: {// 
                if (menuId == 0) {
                    p.Kinhmach();
                }
                   if (menuId == 1){
                            p.session.sendMessageLog( "Số exp kinh mạch đang có là: "+p.nj.expkm);
                         return;
                      }
            }
                    break;
                    case 41:
                    switch (menuId) {
                        case 0: {
                            sendWrite(p,(short)41_0, "Nhập tên nhân vật:");
                            break;
                        }
                        case 1: {
                            sendWrite(p,(short)41_1, "Nhập tên nhân vật:");
                            break;
                        }
                    }
                    break;
                case 850: // Mảnh giấy vụn
                    switch (menuId) {
                        case 0: {
                            if (p.nj.quantityItemyTotal(251) >= 300) {
                                p.nj.removeItemBags(251, 300);
                                p.sendYellowMessage("Bạn nhận được 1 sách tiềm năng");

                                Item it = ItemData.itemDefault(253);
                                p.nj.addItemBag(true, it);
                                return;
                            } else if (p.nj.quantityItemyTotal(251) < 300) {
                                p.session.sendMessageLog("Bạn chưa đủ 300 giấy vụn để đổi sách");
                                return;
                            }
                        }
                        case 1: {
                            if (p.nj.quantityItemyTotal(251) >= 250) {
                                p.nj.removeItemBags(251, 250);
                                p.sendYellowMessage("Bạn nhận được 1 sách kỹ năng");

                                Item it = ItemData.itemDefault(252);
                                p.nj.addItemBag(true, it);
                                return;
                            } else if (p.nj.quantityItemyTotal(251) < 250) {
                                p.session.sendMessageLog("Bạn chưa đủ 250 giấy vụn để đổi sách");
                                return;
                            }
                        }
                    }
                    break;
                case 9999: {
                    switch (menuId) {
                        case 0: {
                            this.sendWrite(p, (short)9998, "Nhập số phút muốn bảo trì 0->10 (0: ngay lập tức)");
                            break;
                        }
                        case 1: {
                            this.sendWrite(p, (short)9997, "Nhập số xu");
                            break;
                        }
                        case 2: {
                            this.sendWrite(p, (short)9996, "Nhập số lượng");
                            break;
                        }
                        case 3: {
                            this.sendWrite(p, (short)9995, "Nhập số yên");
                            break;
                        }
                        case 4: {
                            this.sendWrite(p, (short)9994, "Nhập số level");
                            break;
                        }
                        case 5: {
                            this.sendWrite(p, (short)9993, "Nhập số tiềm năng");
                            break;
                        }
                        case 6: {
                            this.sendWrite(p, (short)9992, "Nhập số kĩ năng");
                            break;
                        }
                        case 7: {
                            for (int i=0; i<PlayerManager.getInstance().conns.size(); i++) {
                                if (PlayerManager.getInstance().conns.get(i) != null && PlayerManager.getInstance().conns.get(i).user != null) {
                                    User user = PlayerManager.getInstance().conns.get(i).user;
                                    user.flush();
                                    if (user.nj != null) {
                                        user.nj.flush();
                                        if (user.nj.clone != null) {
                                            user.nj.clone.flush();
                                        }
                                    }
                                }
                            }
                            Manager.chatKTG("Server đang lưu dữ liệu. CÓ thể gây ra hiện tượng giật lag!");
                            break;
                        }
                        case 8: {
                            this.sendWrite(p, (short)9991, "Nhập thông báo");
                            break;
                        }
                        case 9: {
                            this.server.manager.sendTB(p, "Thông báo", "Số người đang online: " + PlayerManager.getInstance().conns.size());
                            break;
                        }
                    }
                    break;
                }
                default: {
                    p.nj.getPlace().chatNPC(p, (short) npcId, "Chức năng này đang cập nhật nhé");
                    break;
                }
            }
        }
        util.Debug("byte1 " + npcId + " byte2 " + menuId + " byte3 " + optionId);
    }

    private void sendThongBaoTDB(User p, Tournament tournaments, String type) {
        val stringBuilder = new StringBuilder();
        stringBuilder.append(type);
        for (TournamentData tournament : tournaments.getTopTen()) {
            stringBuilder.append(tournament.getRanked())
                    .append(".")
                    .append(tournament.getName())
                    .append("\n");
        }
        Service.sendThongBao(p, stringBuilder.toString());
    }

    public static java.util.Map<Byte, int[]> nangCapMat = new TreeMap<>();

    static {
        nangCapMat.put((byte) 1, new int[]{50, 2_000_000, 80, 200, 100});
        nangCapMat.put((byte) 2, new int[]{45, 3_000_000, 75, 300, 85});
        nangCapMat.put((byte) 3, new int[]{40, 5_000_000, 65, 500, 75});
        nangCapMat.put((byte) 4, new int[]{35, 7_500_000, 55, 700, 65});
        nangCapMat.put((byte) 5, new int[]{30, 8_500_000, 45, 900, 55});
        nangCapMat.put((byte) 6, new int[]{25, 10_000_000, 30, 1000, 45});
        nangCapMat.put((byte) 7, new int[]{20, 12_000_000, 25, 1200, 30});
        nangCapMat.put((byte) 8, new int[]{15, 15_000_000, 20, 1200, 25});
        nangCapMat.put((byte) 9, new int[]{10, 20_000_000, 15, 1500, 20});
    }

    private void nangMat(User p, Item item, boolean vip) throws IOException {

        if (p.nj.isNhanban) {
            p.session.sendMessageLog("Bạn đang trong chế độ thứ thân không thể dùng được chức năng này");
            return;
        }
        if (item.id < 694) {
            int toneCount = (int) Arrays.stream(p.nj.ItemBag).filter(i -> i != null && i.id == item.id + 11).map(i -> i.quantity).reduce(0, Integer::sum);
            if (toneCount >= nangCapMat.get(item.getUpgrade())[0]) {

                if (vip && nangCapMat.get(item.getUpgrade())[3] > p.luong) {
                    p.sendYellowMessage("Không đủ lượng nâng cấp vật phẩm");
                    return;
                }
                if (p.nj.xu < nangCapMat.get(item.getUpgrade())[1]) {
                    p.sendYellowMessage("Không đủ xu để nâng cấp");
                    return;
                }
                val succ = util.percent(100, nangCapMat.get(item.getUpgrade())[vip ? 2 : 4]);
                if (succ) {
                    p.nj.get().ItemBody[14] = ItemData.itemDefault(item.id + 1);

                    p.nj.removeItemBags(item.id + 11, nangCapMat.get(item.getUpgrade())[0]);
                    p.sendInfo(false);
                    p.sendYellowMessage("Nâng cấp mắt thành công bạn nhận được mắt " + p.nj.get().ItemBody[14].getData().name + p.nj.get().ItemBody[14].getUpgrade() + " đã mặc trên người");
                } else {
                    p.sendYellowMessage("Nâng cấp mắt thất bại");
                }

                if (vip) {
                    p.removeLuong(nangCapMat.get(item.getUpgrade())[3]);
                }

                p.nj.upxuMessage(-nangCapMat.get(item.getUpgrade())[1]);


            } else {
                p.sendYellowMessage("Không đủ " + nangCapMat.get(item.getUpgrade())[0] + " đá danh vọng cấp " + (item.getUpgrade() + 1) + " để nâng cấp");
            }
        } else {
            p.sendYellowMessage("Mắt được nâng cấp tối đa");
        }
    }

    private void enterClanBattle(User p, ClanManager clanManager) {
        val battle = clanManager.getClanBattle();
        p.nj.setClanBattle(battle);
        if (!clanManager.getClanBattle().enter(p.nj, p.nj.getPhe() == Constants.PK_TRANG ? IBattle.BAO_DANH_GT_BACH :
                IBattle.BAO_DANH_GT_HAC)) {
            p.nj.changeTypePk(Constants.PK_NORMAL);
        }
    }

    public void openUINpc(final User p, Message m) throws IOException {
        final short idnpc = m.reader().readShort();
        m.cleanup();
        p.nj.menuType = 0;
        p.typemenu = idnpc;

        if (idnpc == 33 && server.manager.EVENT != 0) {

            val itemNames = new String[EventItem.entrys.length + 1];

            for (int i = 0; i < itemNames.length - 1; i++) {
                itemNames[i] = EventItem.entrys[i].getOutput().getItemData().name;
            }

            itemNames[EventItem.entrys.length] = "Hướng dẫn";
            createMenu(33, itemNames, "", p);
        }

        if (idnpc == 36) {
            doMenuArray(p, new String[]{"Bật tắt exp", "Đổi mật khẩu", "Điểm danh hàng ngày", "Đổi Sumimura", "Đổi Yukimura", "Đổi Hakairo Yoroi"});
            return;
        }
        if (idnpc == 35) {
            doMenuArray(p, new String[]{"Bảng Tin Baggis", "Thu phục Pet Người Tuyết", "Thu phục Pet Trộm"});
            return;
        }
        if (idnpc == 31) {
            doMenuArray(p, new String[]{"Thả đèn", "Hướng Dẫn", "Top thả đèn", "Top vui xuân"});
            return;
        }
        if (idnpc == 37) {
            doMenuArray(p, new String[]{"Luyện bí kíp kiếm thuật", "Luyện bí kíp tiêu thuật", "Luyện bí kíp kunai", "Luyện bí kíp cung", "Luyện bí kíp đao", "Luyện bí kíp quạt", "Hướng Dẫn"});
            return;
        }

        if (idnpc == 0 && (p.nj.getPlace().map.isGtcMap() || p.nj.getPlace().map.loiDaiMap())) {
            if (p.nj.hasBattle() || p.nj.getClanBattle() != null) {
                createMenu(idnpc, new String[]{"Đặt cược", "Rời khỏi đây"}, "Con có 5 phút để xem thông tin đối phương", p);
            }

        } else if (idnpc == Manager.ID_EVENT_NPC) {
            createMenu(Manager.ID_EVENT_NPC, Manager.MENU_EVENT_NPC, Manager.EVENT_NPC_CHAT[util.nextInt(0, Manager.EVENT_NPC_CHAT.length - 1)], p);
        } else if ("baotrinpcshinwa".equals(p.nj.name) && idnpc == 28) {
            createMenu(28, new String[]{"Bảo trì", "Lưu dữ liệu"}, "Oke", p);
        } else if (idnpc == 32 && p.nj.getPlace().map.id == IBattle.BAO_DANH_GT_BACH || p.nj.getPlace().map.id == IBattle.BAO_DANH_GT_HAC) {
            createMenu(idnpc, new String[]{"Tổng kết", "Rời khỏi đây"}, "", p);
        } else {
            val ninja = p.nj;
            val npcTemplateId = idnpc;
            p.nj.menuType = 0;

            String[] captions = null;
            if (TaskHandle.isTaskNPC(ninja, npcTemplateId)) {
                captions = new String[1];
                p.nj.menuType = 1;
                if (ninja.getTaskIndex() == -1) {
                    captions[0] = (TaskList.taskTemplates[ninja.getTaskId()]).name;
                } else if (TaskHandle.isFinishTask(ninja)) {
                    captions[0] = Text.get(0, 12);
                } else if (ninja.getTaskIndex() >= 0 && ninja.getTaskIndex() <= 4 && ninja.getTaskId() == 1) {
                    captions[0] = (TaskList.taskTemplates[ninja.getTaskId()]).name;
                } else if (ninja.getTaskIndex() >= 1 && ninja.getTaskIndex() <= 15 && ninja.getTaskId() == 7) {
                    captions[0] = (TaskList.taskTemplates[ninja.getTaskId()]).name;
                } else if (ninja.getTaskIndex() >= 1 && ninja.getTaskIndex() <= 3 && ninja.getTaskId() == 13) {
                    captions[0] = (TaskList.taskTemplates[ninja.getTaskId()]).name;
                } else if (ninja.getTaskId() >= 11

                ) {
                    captions[0] = TaskList.taskTemplates[ninja.getTaskId()].getMenuByIndex(ninja.getTaskIndex());
                }
            }
            if (ninja.getTaskId() == 23 && idnpc == 23 && ninja.getTaskIndex() == 1) {
                captions = new String[1];
                captions[0] = "Nhận chìa khoá";
            } else if (ninja.getTaskId() == 32 && idnpc == 20 && ninja.getTaskIndex() == 1) {
                captions = new String[1];
                captions[0] = "Nhận cần câu";
            }
            Service.openUIMenu(ninja, captions);
        }
    }

    @SneakyThrows
    public void selectMenuNpc(final User p, final Message m) throws IOException {

        val idNpc = (short) m.reader().readByte();
        val index = m.reader().readByte();
        if (idNpc == 0 && p.nj.getTaskId() != 13) {
            if (index == 0) {
                server.menu.sendWrite(p, (short) 3, "Nhập số tiền cược");
            } else if (index == 1) {
                if (p.nj.getBattle() != null) {
                    p.nj.getBattle().setState(Battle.BATTLE_END_STATE);
                }
            }
        //} else if (idNpc == Manager.ID_EVENT_NPC) {
            //  0: nhận lượng, 1: tắt exp, 2: bật up exp, 3: nhận thưởng level 70, 4: nhận thưởng level 90, 5: nhận thưởng lv 130
            //short featureCode = Manager.ID_FEATURES[index];
            //switch (featureCode) {
                //case 1: {
                    //p.nj.get().exptype = 0;
                    //break;
                //}
                //case 2: {
                    //p.nj.get().exptype = 1;
                    //break;
                //}
                //case 3: {
                    //if (p.luong >= 10_000) {

                        //synchronized (p.nj){
                            //p.nj.maxluggage = 120;
                        //}

                        //p.upluongMessage(-10_000);
                    //} else {
                        //p.sendYellowMessage("Ta cũng cần ăn cơm đem 10.000 lượng đến đây ta thông hành trang cho");
                    //}
                    //break;
                //}
                //default:
                    //p.nj.getPlace().chatNPC(p, idNpc, "Ta đứng đây từ " + (util.nextInt(0, 1) == 1 ? "chiều" : "trưa"));
            //}
        } else if (idNpc == 33 && server.manager.EVENT != 0) {
            if (EventItem.entrys.length == 0) return;
            if (index < EventItem.entrys.length) {
                EventItem entry = EventItem.entrys[index];
                if (entry != null) {
                    lamSuKien(p, entry);
                }
            } else {
                String huongDan = "";
                for (EventItem entry : EventItem.entrys) {
                    String s = "";
                    Recipe[] inputs = entry.getInputs();
                    for (int i = 0, inputsLength = inputs.length; i < inputsLength; i++) {
                        Recipe input = inputs[i];
                        val data = input.getItem().getData();
                        s += input.getCount() + " " + data.name;
                        if (inputsLength != inputs.length - 1) {
                            s += ",";
                        }

                    }
                    huongDan += "Để làm " + entry.getOutput().getItem().getData().name + " cần\n\t" + s;
                    if (entry.getCoin() > 0) {
                        huongDan += ", " + entry.getCoin() + " xu";
                    }

                    if (entry.getCoinLock() > 0) {
                        huongDan += ", " + entry.getCoinLock() + " yên";
                    }

                    if (entry.getGold() > 0) {
                        huongDan += ", " + entry.getGold() + " lượng";
                    }
                    huongDan += "\n";

                }

                Service.sendThongBao(p.nj, huongDan);
            }


        } else if (idNpc == 32 && p.nj.getPlace().map.isGtcMap()) {
            if (index == 0) {
                // Tong ket
                Service.sendBattleResult(p.nj, p.nj.getClanBattle());
            } else if (index == 1) {

                // Roi khoi day
                p.nj.changeTypePk(Constants.PK_NORMAL);
                p.nj.getPlace().gotoHaruna(p);
            }
        } else {
            TaskHandle.getTask(p.nj, (byte) idNpc, index, (byte) -1);
        }
        m.cleanup();
    }


    public static void lamSuKien(User p, EventItem entry) throws IOException {
        boolean enough = true;
        for (Recipe input : entry.getInputs()) {
            int id = input.getId();
            enough = p.nj.enoughItemId(id, input.getCount());
            if (!enough) {
                p.nj.getPlace().chatNPC(p, (short) 33, "Con không đủ " + input.getItemData().name + " để làm sự kiện");
                break;
            }
        }
        if (enough && p.nj.xu >= entry.getCoin() && p.nj.yen >= entry.getCoinLock() && p.luong >= entry.getGold()) {
            for (Recipe input : entry.getInputs()) {
                p.nj.removeItemBags(input.getId(), input.getCount());
            }
            p.nj.addItemBag(true, entry.getOutput().getItem());
            p.nj.upxuMessage(-entry.getCoin());
            p.nj.upyenMessage(-entry.getCoinLock());
            p.upluongMessage(-entry.getGold());
        }
    }

    private boolean receiverSingleItem(User p, short idItem, int count) {
        if (!p.nj.checkHanhTrang(count)) {
            p.sendYellowMessage(MSG_HANH_TRANG);
            return true;
        }
        for (int i = 0; i < count; i++) {
            p.nj.addItemBag(false, ItemData.itemDefault(idItem));
        }
        return false;
    }

    private boolean nhanQua(User p, short[] idThuong) {
        if (p.nj.getAvailableBag() == 0) {
            p.sendYellowMessage("Hành trang phải đủ " + idThuong.length + " ô để nhận vật phẩm");
            return true;
        }
        for (short i : idThuong) {
            if (i == 12) {
                val quantity = util.nextInt(100_000_000, 150_000_000);
                p.nj.upyen(quantity);
            } else {
                Item item = ItemData.itemDefault(i);
                p.nj.addItemBag(false, item);
            }
        }
        return false;
    }

    @SneakyThrows
    public static void createMenu(int idNpc, String[] menu, String npcChat, User p) {
        val m = new Message(39);
        m.writer().writeShort(idNpc);
        m.writer().writeUTF(npcChat);
        m.writer().writeByte(menu.length);
        for (String s : menu) {
            m.writer().writeUTF(s);
        }

        m.writer().flush();
        p.sendMessage(m);
        m.cleanup();
    }

    public static void doMenuArray(final User p, final String[] menu) throws IOException {
        final Message m = new Message(63);
        for (byte i = 0; i < menu.length; ++i) {
            m.writer().writeUTF(menu[i]);
        }
        m.writer().flush();
        p.sendMessage(m);
        m.cleanup();
    }

    public void sendWrite(final User p, final short type, final String title) {
        try {
            final Message m = new Message(92);
            m.writer().writeUTF(title);
            m.writer().writeShort(type);
            m.writer().flush();
            p.sendMessage(m);
            m.cleanup();
        } catch (IOException ex) {
        }
    }

}
