package com.vgtech.common;

/**
 * Created by vic on 2017/1/13.
 */
public interface URLAddr {
    String SCHEME = "https";
    String IP = "app.hyylkj.com"; //生产环境
    String PORT = "80";
    String HOST = "http://app.hyylkj.com/";
    String URL_VCHAT_PNS = "v%1$d/vchat/pns";
    String URL_VCHAT_GROUPMEMBERS = "v%1$d/vchat/xmpp/groupmembers";
    String URL_VCHAT_MUCOWNER = "v%1$d/vchat/xmpp/mucowner";
    String URL_IMAGE = HOST+"file/uploadChatFile";
    String URL_AUDIO = HOST+"file/uploadChatFile";
    String URL_LOGIN = HOST+"login/doLogin";
    String URL_MSG_SEND = HOST+"msg/send";
    String URL_REG_DOREG = HOST+"reg/doReg";
    String URL_PRODUCT_PRODUCTCATEGORYLIST = HOST+"product/productCategoryList";
    String URL_MOBILE_NEWMOBILE = HOST+"mobile/newMobile";
    String URL_MOBILE_VERIFICATIONCODE = HOST+"mobile/verificationCode";
    String URL_ACCOUNT_GETBACKPASSWORD = HOST+"account/getBackPassword";

    String URL_INDEX = HOST+"index";
    String URL_RESERVATION_SELECTWORKSHIFTS = HOST+"reservation/selectWorkShifts";
    String URL_INDEXSEARCH = HOST+"indexSearch";
    String URL_SCALE_SCALETYPELIST = HOST+"scale/scaleTypeList";
    String URL_SCALE_INDEX = HOST+"scale/index";
    String URL_SCALE_MYSCALE = HOST+"scale/myScale";
    String URL_PRODUCT_MYPRODUCT = HOST+"product/myProduct";
    String URL_SCALE_DETAIL = HOST+"scale/detail";
    String URL_MY_PROFILE = HOST+"my/profile";
    String URL_MY_PROFILESAVE = HOST+"my/profileSave";
    String URL_MY_UPLOADHEADIMG = HOST+"my/uploadHeadImg";
    String URL_TEST_TESTBEGIN = HOST+"test/testBegin";//开始答题
    String URL_TEST_QUESTIONVIEW = HOST+"test/questionView";//进入答题页面
    String URL_TEST_SUBMITANSWER = HOST+"test/submitAnswer";
    String URL_COUNSELOR_INDEX = HOST+"counselor/index";
    String URL_REPORT_REPORTLIST = HOST+"report/reportList";
    String URL_COUNSELOR_DETAIL = HOST+"counselor/detail";
    String URL_PRODUCT_INDEX = HOST+"product/index";
    String URL_MY_HOME = HOST+"my/home";
    String URL_RESERVATION_COUNRESERVATIONLIST = HOST+"reservation/counReservationList";
    String URL_RESERVATION_COUNRESERVATIONLIST4COUNSELOR = HOST+"reservation/counReservationList4Counselor";
    String URL_PRODUCT_DETAIL = HOST+"product/detail";
    String URL_MY_REPORTVIEW = HOST+"my/reportView";
    String URL_COUNSELOR_DOPRAISE = HOST+"counselor/doPraise";
    String URL_ATTENTION_DOATTENTION = HOST+"attention/doAttention";
    String URL_RESERVATION_TESTRESERVATIONSAVE = HOST+"reservation/testReservationSave";
    String URL_RESERVATION_COUNSELORRESERVATIONSAVE = HOST+"reservation/counselorReservationSave";
    String URL_RESERVATION_COUNRESERVATIONVIEW = HOST+"reservation/counReservationView";
    String URL_RESERVATION_COUNRESERVATIONVIEWINFO = HOST+"reservation/counReservationViewInfo";
    String URL_RESERVATION_COUNSELORSTART = HOST+"reservation/counselorStart";
    String URL_RESERVATION_COUNSELORSAVERESERVATIONINFO = HOST+"reservation/counselorSaveReservationInfo";

    String URL_TAB_INDEX = HOST+"index.html";
    String URL_TAB_SCALE = HOST+"html/scale.html";
    String URL_TAB_COUNSELOR = HOST+"html/counselor.html";
    String URL_TAB_PRODUCT = HOST+"html/product.html";
    String URL_TAB_HOME = HOST+"html/home.html";
}
