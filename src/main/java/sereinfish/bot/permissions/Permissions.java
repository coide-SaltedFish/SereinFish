package sereinfish.bot.permissions;

import com.icecreamqaq.yuq.entity.Group;
import com.icecreamqaq.yuq.entity.Member;
import lombok.Getter;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 权限管理器
 */
public class Permissions {
    public static final int ALL_GROUP = -1;//所有群组

    public static final int OP = -1;//服务器op权限
    public static final int SYSTEM = 0;//系统级权限
    public static final int MASTER = 1;//拥有者权限
    public static final int ADMIN = 2;//bot管理员权限
    public static final int GROUP_MASTER = 3;//群主权限
    public static final int GROUP_BOT_ADMIN = 4;//群bot管理权限
    public static final int GROUP_ADMIN = 5;//群管理权限
    public static final int NORMAL = 6;//普通权限

    public static final Map<String, Integer> AuthorityList = new HashMap<>();//权限值映射表
    public static final Map<String, Integer> dynamicPermissionList = new LinkedHashMap<>();//可命令动态修改的权限列表

    private static Permissions permissions;//单例模式
    private AuthorityList authorityList;//权限列表类

    private Permissions() throws IOException {
        //初始化映射表
        AuthorityList.put("OP", OP);
        AuthorityList.put("SYSTEM", SYSTEM);
        AuthorityList.put("MASTER", MASTER);
        AuthorityList.put("ADMIN", ADMIN);
        AuthorityList.put("GROUP_MASTER", GROUP_MASTER);
        AuthorityList.put("GROUP_BOT_ADMIN", GROUP_BOT_ADMIN);
        AuthorityList.put("GROUP_ADMIN", GROUP_ADMIN);
        AuthorityList.put("NORMAL", NORMAL);
        //初始化可动态修改的权限的映射表
        dynamicPermissionList.put("OP", OP);
        dynamicPermissionList.put("ADMIN", ADMIN);
        dynamicPermissionList.put("GROUP_BOT_ADMIN", GROUP_BOT_ADMIN);

        readAuthorityList();//得到权限列表
    }

    public static Permissions init() throws IOException {
        permissions = new Permissions();
        return permissions;
    }

    public static Permissions getInstance(){
        if (permissions == null){
            throw new NullPointerException("权限管理器未初始化");
        }
        return permissions;
    }

    /**
     * 添加权限组
     * @param group
     * @param qq
     * @param permission
     * @return
     */
    public void addPermission(long group, long qq, int permission) throws IllegalStateException{
        switch (permission){
            case ADMIN:
                authorityList.addAdmin(qq);
                break;
            case OP:
                authorityList.addOP(group, qq);
                break;
            case GROUP_BOT_ADMIN:
                authorityList.addGroupBotAdmin(group, qq);
                break;
            default:
                throw new IllegalStateException("添加失败，可能是权限值错误或该权限并不支持动态添加:" + permission);
        }
    }

    /**
     * 通过qq得到用户所在权限组
     * @param member
     * @return
     */
    public Integer[] getMemberPermissions(Group group, Member member){
        ArrayList<Integer> permissions = new ArrayList<>();
        try {
            readAuthorityList();//得到权限列表
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(), "权限列表更新失败，使用缓存中", e);
        }
        if (authorityList.isOP(group.getId(), member.getId())){
            permissions.add(OP);
        }

        if (authorityList.isMaster(member.getId())){
            permissions.add(MASTER);
        }else if (authorityList.isAdmin(member.getId())){
            permissions.add(ADMIN);
        }else if(member.isOwner()){
            permissions.add(GROUP_MASTER);
        }else if (member.isAdmin()){
            permissions.add(GROUP_ADMIN);
        }else if(authorityList.isGroupBotAdmin(group.getId(), member.getId())){
            permissions.add(GROUP_BOT_ADMIN);
        }else{
            permissions.add(NORMAL);
        }

        return permissions.toArray(new Integer[]{});
    }

    /**
     * 得到权限字段
     * @param var
     * @return
     */
    public String getAuthorityName(int var){
        for (Map.Entry<String, Integer> entry:AuthorityList.entrySet()){
            if (entry.getValue() == var){
                return entry.getKey();
            }
        }
        return "未知";
    }

    /**
     * 得到权限值
     * @param var
     * @return
     */
    public int getAuthorityValue(String var) throws IllegalStateException{
        for (Map.Entry<String, Integer> entry:AuthorityList.entrySet()){
            if (entry.getKey().equals(var)){
                return entry.getValue();
            }
        }
        throw new IllegalStateException("未知权限字段:" + var);
    }

    /**
     * 判断是否可以操作权限
     * @param member
     * @param permission
     * @return
     */
    public boolean isOperation(Group group, Member member, int permission){
        boolean flag = false;
        for(int p:getMemberPermissions(group, member)){
            if (flag == true){
                break;
            }

            if (p == OP){
                flag = false;
                continue;
            }

            if (p == MASTER){
                flag = true;
                break;
            }

            if (p < permission){
                flag = true;
            }

        }

        return flag;
    }

    /**
     * 权限检查
     * @return
     */
    public boolean authorityCheck(Integer[] permissions, int authority){
        for (int p:permissions){
            if (p == OP){
                if(p == authority){
                    return true;
                }
            }
            if (p >= 0 && p <= authority){
                return true;
            }
        }
        return false;
    }

    /**
     * 权限检查
     * @param member
     * @return
     */
    public boolean authorityCheck(Group group, Member member, int authority){
        if (member == null){
            SfLog.getInstance().w(this.getClass(),"无法获取发送者信息：null");
            return false;
        }
        try {
            readAuthorityList();//得到权限列表
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(), "权限列表更新失败，使用缓存中", e);
        }

        return authorityCheck(getMemberPermissions(group, member), authority);
    }



    /**
     * 得到权限列表
     */
    private void readAuthorityList() throws IOException {
        authorityList = MyYuQ.toClass(FileHandle.read(FileHandle.AuthorityConfigFile),AuthorityList.class);
        if (authorityList == null){
            authorityList = new AuthorityList();
            writeAuthorityList();
        }
    }

    /**
     * 设置权限列表
     */
    private void writeAuthorityList() throws IOException {
        FileHandle.write(FileHandle.AuthorityConfigFile, MyYuQ.toJson(authorityList,AuthorityList.class));
    }

    public AuthorityList getAuthorityList() {
        return authorityList;
    }

    /**
     * 权限列表类
     */
    @Getter
    public class AuthorityList{
        Map<Long,ArrayList<Long>> opList = new HashMap<>();//op列表
        Map<Long,Long> masterList = new HashMap<>();//拥有者列表
        Map<Long,Long> adminList = new HashMap<>();//管理员列表
        Map<Long, ArrayList<Long>> groupBotAdmin = new HashMap<>();//群bot管理员列表

        /**
         * 是否是OP
         * @param id
         * @return
         */
        public boolean isOP(long group, long id){
            //是否在群组
            if (!opList.containsKey(group)){
                return false;
            }
            //是否为所有群组OP
            if (opList.containsKey(ALL_GROUP)){
                if (opList.get(ALL_GROUP).contains(id)){
                    return true;
                }
            }
            return opList.get(group).contains(id);
        }

        /**
         * op添加
         * @param id
         * @return
         */
        public boolean addOP(long group, long id){
            if (!opList.containsKey(group)){
                opList.put(group, new ArrayList<>());
            }
            if (opList.get(group).contains(id)){
                throw new IllegalStateException("已拥有该权限");
            }else {
                opList.get(group).add(id);
            }
            try {
                Permissions.getInstance().writeAuthorityList();
                return true;
            } catch (IOException e) {
                SfLog.getInstance().e(this.getClass(),"op添加失败:" + id,e);
                return false;
            }
        }

        /**
         * 删除op
         * @return
         */
        public boolean deleteOP(long group, long id){
            if (!isOP(group, id)){
                SfLog.getInstance().e(this.getClass(),"op不存在：" + id);
                return false;
            }
            if (opList.get(group).remove(id)){
                try {
                    Permissions.getInstance().writeAuthorityList();
                    return true;
                } catch (IOException e) {
                    SfLog.getInstance().e(this.getClass(),"op删除失败:" + id,e);
                    return false;
                }
            }
            return false;
        }

        /**
         * 是否属于拥有者
         * @param id
         * @return
         */
        public boolean isMaster(long id){
            return masterList.containsKey(id);
        }

        /**
         * 添加拥有者
         * @param id
         * @return
         */
        public boolean addMaster(long id){
            masterList.put(id,id);
            try {
                Permissions.getInstance().writeAuthorityList();
                return true;
            } catch (IOException e) {
                SfLog.getInstance().e(this.getClass(),"拥有者添加失败：" + id,e);
                return false;
            }
        }

        /**
         * 删除拥有者
         * @param id
         * @return
         */
        public boolean deleteMaster(long id){
            if (!isMaster(id)){
                SfLog.getInstance().e(this.getClass(),"拥有者不存在：" + id);
                return false;
            }
            if(masterList.remove(id,id)){
                try {
                    Permissions.getInstance().writeAuthorityList();
                    return true;
                } catch (IOException e) {
                    SfLog.getInstance().e(this.getClass(),"拥有者删除失败:" + id,e);
                    return false;
                }
            }

            return false;
        }

        /**
         * 是否管理员
         * @param id
         * @return
         */
        public boolean isAdmin(long id){
            return adminList.containsKey(id);
        }

        /**
         * 添加管理员
         * @param id
         * @return
         */
        public boolean addAdmin(long id){
            adminList.put(id,id);
            try {
                Permissions.getInstance().writeAuthorityList();
                return true;
            } catch (IOException e) {
                SfLog.getInstance().e(this.getClass(),"管理员添加失败：" + id,e);
                return false;
            }
        }

        /**
         * 删除管理员
         * @param id
         * @return
         */
        public boolean deleteAdmin(long id){
            if (!isAdmin(id)){
                SfLog.getInstance().e(this.getClass(),"管理员不存在：" + id);
                return false;
            }
            if(adminList.remove(id,id)){
                try {
                    Permissions.getInstance().writeAuthorityList();
                    return true;
                } catch (IOException e) {
                    SfLog.getInstance().e(this.getClass(),"管理员删除失败:" + id,e);
                    return false;
                }
            }
            return false;
        }

        /**
         * 是否是群bot管理员
         * @param id
         * @return
         */
        public boolean isGroupBotAdmin(long group, long id){
            //是否在群组
            if (!groupBotAdmin.containsKey(group)){
                return false;
            }
            //是否为所有群组OP
            if (groupBotAdmin.containsKey(ALL_GROUP)){
                if (groupBotAdmin.get(ALL_GROUP).contains(id)){
                    return true;
                }
            }
            return groupBotAdmin.get(group).contains(id);
        }

        /**
         * 群bot管理员添加
         * @param id
         * @return
         */
        public boolean addGroupBotAdmin(long group, long id){
            if (!groupBotAdmin.containsKey(group)){
                groupBotAdmin.put(group, new ArrayList<>());
            }
            if (groupBotAdmin.get(group).contains(id)){
                throw new IllegalStateException("已拥有该权限");
            }else {
                groupBotAdmin.get(group).add(id);
            }
            try {
                Permissions.getInstance().writeAuthorityList();
                return true;
            } catch (IOException e) {
                SfLog.getInstance().e(this.getClass(),"群bot管理员添加失败:" + id,e);
                return false;
            }
        }

        /**
         * 删除群bot管理员
         * @return
         */
        public boolean deleteGroupBotAdmin(long group, long id){
            if (!isGroupBotAdmin(group, id)){
                SfLog.getInstance().e(this.getClass(),"群bot管理员不存在：" + id);
                return false;
            }
            if (groupBotAdmin.get(group).remove(id)){
                try {
                    Permissions.getInstance().writeAuthorityList();
                    return true;
                } catch (IOException e) {
                    SfLog.getInstance().e(this.getClass(),"群bot管理员删除失败:" + id,e);
                    return false;
                }
            }
            return false;
        }
    }
}
