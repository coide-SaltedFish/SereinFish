package sereinfish.bot.authority;

import com.icecreamqaq.yuq.entity.Member;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 权限管理器
 */
public class AuthorityManagement {
    public static final int OP = -1;//服务器op权限
    public static final int MASTER = 0;//拥有者权限
    public static final int ADMIN = 1;//bot管理员权限
    public static final int GROUP_ADMIN = 2;//群管理权限
    public static final int NORMAL = 3;//普通权限

    private static AuthorityManagement authorityManagement;//单例模式
    private AuthorityList authorityList;//权限列表类
    SfLog sfLog;//日志

    private AuthorityManagement() throws IOException {
        sfLog = SfLog.getInstance();
        readAuthorityList();//得到权限列表
    }

    public static AuthorityManagement init() throws IOException {
        authorityManagement = new AuthorityManagement();
        return authorityManagement;
    }

    public static AuthorityManagement getInstance(){
        if (authorityManagement == null){
            throw new NullPointerException("权限管理器未初始化");
        }
        return authorityManagement;
    }
    /**
     * 权限检查
     * @param member
     * @return
     */
    public boolean authorityCheck(Member member, int authority){
        if (member == null){
            sfLog.w(this.getClass(),"无法获取发送者信息：null");
            return false;
        }
        try {
            readAuthorityList();//得到权限列表
        } catch (IOException e) {
            SfLog.getInstance().e(this.getClass(), "权限列表更新失败，使用缓存中", e);
        }

        if (authority == NORMAL){//普通权限
            return true;
        }else {
            //多权限判断
            if(authority == OP){//op
                if (authorityList.isOP(member.getId())){
                    return true;
                }
            }else {
                switch (authority){
                    case MASTER://拥有者
                        if (authorityList.isMaster(member.getId())){
                            return true;
                        }
                        break;
                    case ADMIN://管理员
                        if (authorityList.isMaster(member.getId())
                                || authorityList.isAdmin(member.getId())){
                            return true;
                        }
                        break;
                    case GROUP_ADMIN://群管理
                        if (authorityList.isMaster(member.getId())
                                || authorityList.isAdmin(member.getId())
                                || member.isAdmin()){
                            return true;
                        }
                        break;
                    default:
                        return false;
                }
            }
        }
        return false;
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
    public class AuthorityList{
        Map<Long,Long> opList = new HashMap<>();//op列表
        Map<Long,Long> masterList = new HashMap<>();//拥有者列表
        Map<Long,Long> adminList = new HashMap<>();//管理员列表

        /**
         * 是否是OP
         * @param id
         * @return
         */
        public boolean isOP(long id){
            return opList.containsKey(id);
        }

        /**
         * op添加
         * @param id
         * @return
         */
        public boolean addOP(long id){
            opList.put(id,id);
            try {
                AuthorityManagement.getInstance().writeAuthorityList();
                return true;
            } catch (IOException e) {
                sfLog.e(this.getClass(),"op添加失败:" + id,e);
                return false;
            }
        }

        /**
         * 删除op
         * @return
         */
        public boolean deleteOP(long id){
            if (!isOP(id)){
                sfLog.e(this.getClass(),"op不存在：" + id);
                return false;
            }
            if (opList.remove(id,id)){
                try {
                    AuthorityManagement.getInstance().writeAuthorityList();
                    return true;
                } catch (IOException e) {
                    sfLog.e(this.getClass(),"op删除失败:" + id,e);
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
                AuthorityManagement.getInstance().writeAuthorityList();
                return true;
            } catch (IOException e) {
                sfLog.e(this.getClass(),"拥有者添加失败：" + id,e);
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
                sfLog.e(this.getClass(),"拥有者不存在：" + id);
                return false;
            }
            if(masterList.remove(id,id)){
                try {
                    AuthorityManagement.getInstance().writeAuthorityList();
                    return true;
                } catch (IOException e) {
                    sfLog.e(this.getClass(),"拥有者删除失败:" + id,e);
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
                AuthorityManagement.getInstance().writeAuthorityList();
                return true;
            } catch (IOException e) {
                sfLog.e(this.getClass(),"管理员添加失败：" + id,e);
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
                sfLog.e(this.getClass(),"管理员不存在：" + id);
                return false;
            }
            if(adminList.remove(id,id)){
                try {
                    AuthorityManagement.getInstance().writeAuthorityList();
                    return true;
                } catch (IOException e) {
                    sfLog.e(this.getClass(),"管理员删除失败:" + id,e);
                    return false;
                }
            }
            return false;
        }


        public Map<Long, Long> getOpList() {
            return opList;
        }

        public Map<Long, Long> getMasterList() {
            return masterList;
        }

        public Map<Long, Long> getAdminList() {
            return adminList;
        }
    }
}
