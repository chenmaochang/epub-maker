package com.cmc.web.dto.response.wenshushu;

import lombok.Data;

import java.io.Serializable;
@Data
public class WenShuShuUserInfo implements Serializable {
    private String uid;
    private String name;
    private String head;
    private String bid;
    private String lang_id;
    private String vip;
    private String vip_end;
    private Vip_info vip_info;
    private String email;
    private String coutry_code;
    private String tel;
    private String intro;
    private String devide_id;
    private boolean password_ex;
    private boolean is_def_head;
    private Sc_acts sc_acts;
    private String fid;
    private String prev_token;
    private boolean once_vip;
    private boolean isAutoUsePd;
    private boolean isAutoUsePayPd;
    private String guide;
    private String box_name;
    private String box_icon;
    private boolean first_login;
    private String wssid;
    private String wssid_next_time;

    @Data
    public class Sc_acts implements Serializable {
        private Qq qq;
        private Weixin weixin;

        @Data
        public class Qq implements Serializable {
            private String platform;
            private String nickname;
        }

        @Data
        public class Weixin implements Serializable {
            private String platform;
            private String nickname;
        }
    }

    @Data
    public class Vip_info implements Serializable {
        private String storage;
        private String prev_dl_size;
        private String up_size;
        private int up_num;
        private String dl_size;
        private boolean dl_arch;
        private String prev_video_size;
        private String prev_img_size;
        private String prev_doc_size;
        private int send_people;
        private int send_def_exp;
        private int send_min_exp;
        private int send_max_exp;
        private boolean send_exp_mod;
        private int recv_people;
        private boolean ad_show;
        private boolean send_arenew;
        private int send_max_files;
        private int collect_max_files;
        private String dl_arch_size;
        private int recycle_keep;
        private int collect_def_exp;
        private int collect_min_exp;
        private int collect_max_exp;
        private boolean collect_modify_exp;
        private boolean collect_not_exp;
        private int save_to_max_num;
        private int dl_arch_max_files;
        private String prev_img_free_size;
        private String prevAudioSize;
        private String prevTxtSize;
        private String freeDownTimeLimit;
        private String freeDownNumLimit;
        private int sendPhonePeople;
        private int recvPhonePeople;
        private String recycle_max_size;
        private int prv_box_num;
        private int share_box_num;
        private int share_key_num;
        private boolean share_key_mod_exp;
        private int share_key_def_exp;
        private int share_key_max_exp;
        private int share_key_min_exp;
        private boolean share_key_auto_renew;
        private int wssid_min_len;
        private String archiveMaxNumberDay;
        private String archiveZipMaxSize;
        private String archiveZipMaxNumber;
        private String archiveUnzipMaxSize;
        private String archiveUnzipMaxNumber;
        private boolean tpl_on_task;
        private boolean tpl_on_sbox;
        private boolean cus_tpl_style;
        private int tpl_max_num;
        private boolean tpl_add_link;
        private int tpl_link_max_num;
    }
}