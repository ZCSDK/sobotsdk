package com.sobot.chat.conversation;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.MarkConfig;
import com.sobot.chat.SobotApi;
import com.sobot.chat.SobotUIConfig;
import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.activity.SobotCameraActivity;
import com.sobot.chat.activity.SobotChooseFileActivity;
import com.sobot.chat.activity.SobotPostLeaveMsgActivity;
import com.sobot.chat.activity.SobotPostMsgActivity;
import com.sobot.chat.activity.SobotSkillGroupActivity;
import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.adapter.SobotMsgAdapter;
import com.sobot.chat.api.apiUtils.SobotVerControl;
import com.sobot.chat.api.apiUtils.ZhiChiConstants;
import com.sobot.chat.api.enumtype.CustomerState;
import com.sobot.chat.api.enumtype.SobotAutoSendMsgMode;
import com.sobot.chat.api.enumtype.SobotChatStatusMode;
import com.sobot.chat.api.model.BaseCode;
import com.sobot.chat.api.model.CommonModel;
import com.sobot.chat.api.model.CommonModelBase;
import com.sobot.chat.api.model.ConsultingContent;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.OrderCardContentModel;
import com.sobot.chat.api.model.SobotCommentParam;
import com.sobot.chat.api.model.SobotConnCusParam;
import com.sobot.chat.api.model.SobotEvaluateModel;
import com.sobot.chat.api.model.SobotKeyWordTransfer;
import com.sobot.chat.api.model.SobotLableInfoList;
import com.sobot.chat.api.model.SobotLocationModel;
import com.sobot.chat.api.model.SobotRobot;
import com.sobot.chat.api.model.SobotTransferOperatorParam;
import com.sobot.chat.api.model.ZhiChiCidsModel;
import com.sobot.chat.api.model.ZhiChiGroup;
import com.sobot.chat.api.model.ZhiChiGroupBase;
import com.sobot.chat.api.model.ZhiChiHistoryMessage;
import com.sobot.chat.api.model.ZhiChiHistoryMessageBase;
import com.sobot.chat.api.model.ZhiChiInitModeBase;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.api.model.ZhiChiPushMessage;
import com.sobot.chat.api.model.ZhiChiReplyAnswer;
import com.sobot.chat.core.channel.Const;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.listener.NoDoubleClickListener;
import com.sobot.chat.listener.PermissionListenerImpl;
import com.sobot.chat.listener.SobotFunctionType;
import com.sobot.chat.presenter.StPostMsgPresenter;
import com.sobot.chat.server.SobotSessionServer;
import com.sobot.chat.utils.AnimationUtil;
import com.sobot.chat.utils.AudioTools;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.CustomToast;
import com.sobot.chat.utils.ExtAudioRecorder;
import com.sobot.chat.utils.ImageUtils;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.MediaFileUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.utils.SobotPathManager;
import com.sobot.chat.utils.StServiceUtils;
import com.sobot.chat.utils.StringUtils;
import com.sobot.chat.utils.TimeTools;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConfig;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.viewHolder.CusEvaluateMessageHolder;
import com.sobot.chat.viewHolder.RichTextMessageHolder;
import com.sobot.chat.viewHolder.RobotTemplateMessageHolder1;
import com.sobot.chat.viewHolder.RobotTemplateMessageHolder2;
import com.sobot.chat.viewHolder.RobotTemplateMessageHolder3;
import com.sobot.chat.viewHolder.RobotTemplateMessageHolder4;
import com.sobot.chat.viewHolder.RobotTemplateMessageHolder5;
import com.sobot.chat.viewHolder.RobotTemplateMessageHolder6;
import com.sobot.chat.viewHolder.VoiceMessageHolder;
import com.sobot.chat.voice.AudioPlayCallBack;
import com.sobot.chat.voice.AudioPlayPresenter;
import com.sobot.chat.widget.ClearHistoryDialog;
import com.sobot.chat.widget.ContainsEmojiEditText;
import com.sobot.chat.widget.DropdownListView;
import com.sobot.chat.widget.dialog.SobotBackDialog;
import com.sobot.chat.widget.dialog.SobotClearHistoryMsgDialog;
import com.sobot.chat.widget.dialog.SobotEvaluateDialog;
import com.sobot.chat.widget.dialog.SobotRobotListDialog;
import com.sobot.chat.widget.emoji.DisplayEmojiRules;
import com.sobot.chat.widget.emoji.EmojiconNew;
import com.sobot.chat.widget.emoji.InputHelper;
import com.sobot.chat.widget.image.SobotRCImageView;
import com.sobot.chat.widget.kpswitch.CustomeChattingPanel;
import com.sobot.chat.widget.kpswitch.util.KPSwitchConflictUtil;
import com.sobot.chat.widget.kpswitch.util.KeyboardUtil;
import com.sobot.chat.widget.kpswitch.view.ChattingPanelEmoticonView;
import com.sobot.chat.widget.kpswitch.view.ChattingPanelUploadView;
import com.sobot.chat.widget.kpswitch.view.CustomeViewFactory;
import com.sobot.chat.widget.kpswitch.widget.KPSwitchFSPanelLinearLayout;
import com.sobot.network.http.callback.StringResultCallBack;
import com.sobot.network.http.upload.SobotUpload;
import com.sobot.pictureframe.SobotBitmapUtil;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static com.sobot.chat.api.enumtype.SobotAutoSendMsgMode.ZCMessageTypeFile;
import static com.sobot.chat.api.enumtype.SobotAutoSendMsgMode.ZCMessageTypePhoto;
import static com.sobot.chat.api.enumtype.SobotAutoSendMsgMode.ZCMessageTypeText;
import static com.sobot.chat.api.enumtype.SobotAutoSendMsgMode.ZCMessageTypeVideo;

/**
 * @author Created by jinxl on 2018/2/1.
 */
public class SobotChatFSFragment extends SobotChatBaseFragment implements View.OnClickListener
        , DropdownListView.OnRefreshListenerHeader, SobotMsgAdapter.SobotMsgCallBack,
        ContainsEmojiEditText.SobotAutoCompleteListener
        , ChattingPanelEmoticonView.SobotEmoticonClickListener
        , ChattingPanelUploadView.SobotPlusClickListener, SobotRobotListDialog.SobotRobotListListener {

    //---------------UI?????? START---------------
    public LinearLayout sobot_header_center_ll;//??????????????????
    public TextView mTitleTextView;//????????????
    public SobotRCImageView mAvatarIV;//??????
    public TextView sobot_title_conn_status;
    public LinearLayout sobot_container_conn_status;
    public TextView sobot_tv_right_second;
    public TextView sobot_tv_right_third;
    public ProgressBar sobot_conn_loading;
    public RelativeLayout net_status_remide;
    public TextView sobot_net_not_connect;
    public RelativeLayout relative;
    private TextView sobot_tv_satisfaction, notReadInfo, sobot_tv_message,
            sobot_txt_restart_talk;
    private TextView textReConnect;
    private ProgressBar loading_anim_view;
    private TextView txt_loading;
    private ImageView icon_nonet;
    private Button btn_reconnect;
    private RelativeLayout chat_main; // ???????????????;
    private FrameLayout welcome; // ????????????;
    private DropdownListView lv_message;/* ????????????ListView */
    private ContainsEmojiEditText et_sendmessage;// ???????????????????????????
    private Button btn_send; // ??????????????????
    private ImageButton btn_set_mode_rengong; // ?????????button
    private View view_model_split;//??????????????????,??????????????????????????????
    private TextView send_voice_robot_hint;
    private Button btn_upload_view; // ????????????
    private ImageButton btn_emoticon_view; // ????????????
    private TextView voice_time_long;/*??????????????????*/
    private LinearLayout voice_top_image;
    private ImageView image_endVoice;
    private ImageView mic_image;
    private ImageView mic_image_animate; // ???????????????
    private ImageView recording_timeshort;// ?????????????????????
    private ImageButton btn_model_edit; // ????????????
    private ImageButton btn_model_voice;// ????????????
    private TextView txt_speak_content; // ?????????????????????
    private AnimationDrawable animationDrawable;/* ??????????????? */
    private KPSwitchFSPanelLinearLayout mPanelRoot; // ?????????????????????
    private LinearLayout btn_press_to_speak; // ??????view ;
    private LinearLayout edittext_layout; // ?????????view;
    private LinearLayout recording_container;// ?????????????????????
    private TextView recording_hint;// ????????????????????????
    private RelativeLayout sobot_ll_restart_talk; // ?????????????????????ID
    private ImageView image_reLoading;
    private LinearLayout sobot_ll_bottom;//????????????????????????
    //??????
    private RelativeLayout sobot_announcement; // ??????view ;
    private TextView sobot_announcement_right_icon;
    private TextView sobot_announcement_title;
    //?????????????????????
    private LinearLayout sobot_ll_switch_robot;
    private TextView sobot_tv_switch_robot;

    private SobotEvaluateDialog mEvaluateDialog;
    private SobotRobotListDialog mRobotListDialog;

    private HorizontalScrollView sobot_custom_menu;//??????????????????
    private LinearLayout sobot_custom_menu_linearlayout;
    private TextView sobot_tv_close;
    //---------------UI?????? END---------------


    //-----------
    // ??????????????????
    private List<ZhiChiMessageBase> messageList = new ArrayList<ZhiChiMessageBase>();


    //--------

    private int showTimeVisiableCustomBtn = 0;/*???????????????????????????????????????*/
    private List<ZhiChiGroupBase> list_group;

    protected int type = -1;//?????????????????????
    private static String preCurrentCid = null;//?????????????????????cid???
    private static int statusFlag = 0; // ????????????????????????????????????
    private boolean isSessionOver = true;//???????????????????????????null

    private boolean isComment = false;/* ??????????????????????????? */
    private boolean isShowQueueTip = true;//???????????? ???????????? ????????????????????????????????????????????????
    private int queueNum = 0;//???????????????
    private int queueTimes = 0;//???????????????????????????????????????
    private int mUnreadNum = 0;//???????????????

    //????????????
    protected Timer voiceTimer;
    protected TimerTask voiceTimerTask;
    protected int voiceTimerLong = 0;
    protected String voiceTimeLongStr = "00";// ????????????????????????
    private int minRecordTime = 60;// ??????????????????
    private int recordDownTime = minRecordTime - 10;// ?????????????????? ?????????
    boolean isCutVoice;
    private String voiceMsgId = "";//  ???????????????Id
    private int currentVoiceLong = 0;

    AudioPlayPresenter mAudioPlayPresenter = null;
    AudioPlayCallBack mAudioPlayCallBack = null;
    private String mFileName = null;
    private ExtAudioRecorder extAudioRecorder;

    //??????????????????????????????????????????
    private List<String> cids = new ArrayList<>();//cid?????????
    private int currentCidPosition = 0;//?????????????????????????????????cid??????
    //????????????cid????????? ?????????????????? 0???????????? 1???????????? 2???????????????  3???????????????
    private int queryCidsStatus = ZhiChiConstant.QUERY_CIDS_STATUS_INITIAL;
    private boolean isInGethistory = false;//????????????????????????????????????
    private boolean isConnCustomerService = false;//?????????????????? ??????????????????????????????
    private boolean isNoMoreHistoryMsg = false;

    //????????????
    public int currentPanelId = 0;//????????????????????? ?????????????????????id ???????????????????????????view???
    private int mBottomViewtype = 0;//?????????????????????

    //---------
    //????????????
    private ViewTreeObserver.OnGlobalLayoutListener mKPSwitchListener;

    private MyMessageReceiver receiver;
    //?????????????????????????????????
    private LocalBroadcastManager localBroadcastManager;
    private LocalReceiver localReceiver;

    //????????????
    private StPostMsgPresenter mPostMsgPresenter;

    //2.9.2?????? ?????????????????????????????????????????????????????????
    private int offlineMsgConnectFlag;
    private String offlineMsgAdminId;
    //?????????????????????????????????????????????????????????
    ZhiChiMessageBase keyWordMessageBase;

    String tempMsgContent;//2.9.3 ?????????/??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

    //??????????????????????????????????????????????????????
    private boolean isAppInitEnd = true;

    public static SobotChatFSFragment newInstance(Bundle info) {
        Bundle arguments = new Bundle();
        arguments.putBundle(ZhiChiConstant.SOBOT_BUNDLE_INFORMATION, info);
        SobotChatFSFragment fragment = new SobotChatFSFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i("onCreate");
        if (getArguments() != null) {
            Bundle informationBundle = getArguments().getBundle(ZhiChiConstant.SOBOT_BUNDLE_INFORMATION);
            if (informationBundle != null) {
                Serializable sobot_info = informationBundle.getSerializable(ZhiChiConstant.SOBOT_BUNDLE_INFO);
                if (sobot_info != null && sobot_info instanceof Information) {
                    info = (Information) sobot_info;
                }
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(getResLayoutId("sobot_chat_fs_fragment"), container, false);
        initView(root);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (info == null) {
            LogUtils.e("???????????????????????????");
            finish();
            return;
        }

        if (SobotVerControl.isPlatformVer) {
            if (TextUtils.isEmpty(info.getApp_key()) && TextUtils.isEmpty(info.getCustomer_code())) {
                LogUtils.i("appkey??????customCode??????????????????");
                finish();
                return;
            }
        } else {
            if (TextUtils.isEmpty(info.getApp_key())) {
                LogUtils.e("??????AppKey??????");
                finish();
                return;
            }
        }

        SharedPreferencesUtil.saveStringData(mAppContext, ZhiChiConstant.SOBOT_CURRENT_IM_APPID, info.getApp_key());

        //?????????????????????
        ChatUtils.saveOptionSet(mAppContext, info);

        initData();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (sobot_tv_close != null) {
            if (info.isShowCloseBtn() && current_client_model == ZhiChiConstant.client_model_customService) {
                //??????????????????????????????
                sobot_tv_close.setVisibility(View.VISIBLE);
            } else {
                sobot_tv_close.setVisibility(View.GONE);
            }
        }
        SharedPreferencesUtil.saveStringData(mAppContext, ZhiChiConstant.SOBOT_CURRENT_IM_APPID, info.getApp_key());
        Intent intent = new Intent(mAppContext, SobotSessionServer.class);
        intent.putExtra(ZhiChiConstant.SOBOT_CURRENT_IM_PARTNERID, info.getPartnerid());
        StServiceUtils.safeStartService(mAppContext, intent);
        SobotMsgManager.getInstance(mAppContext).getConfig(info.getApp_key()).clearCache();
        //???????????????????????????
        if (customerState == CustomerState.Online || customerState == CustomerState.Queuing) {
            //??????tcp????????????????????????????????????0????????????????????????????????????????????????
            long lastHideTime = SharedPreferencesUtil.getLongData(mAppContext, ZhiChiConstant.SOBOT_HIDE_CHATPAGE_TIME, System.currentTimeMillis());
            if (lastHideTime != 0 && !CommonUtils.isServiceWork(getSobotActivity(), "com.sobot.chat.core.channel.SobotTCPServer")) {
                //LogUtils.i((System.currentTimeMillis() + "-------------" + lastHideTime + "==========" + (System.currentTimeMillis() - lastHideTime)));
                // LogUtils.i("----???????????? SobotTCPServer ????????????");
                if ((System.currentTimeMillis() - lastHideTime) > 30 * 60 * 1000) {
                    //   LogUtils.i("----??????SobotTCPServer ??????????????????30??????????????????????????????---------");
                    initSdk(true, 0);
                } else {
                    zhiChiApi.reconnectChannel();
                }
            } else {
                zhiChiApi.reconnectChannel();
            }
        }
    }

    @Override
    public void onPause() {
        if (initModel != null) {
            if (!isSessionOver) {
                //??????????????????
                saveCache();
            } else {
                //??????????????????
                clearCache();
            }
            //??????????????????
            ChatUtils.saveLastMsgInfo(mAppContext, info, info.getApp_key(), initModel, messageList);
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (!isAboveZero) {
            SharedPreferencesUtil.saveLongData(getSobotActivity(), ZhiChiConstant.SOBOT_FINISH_CURTIME, System.currentTimeMillis());
        }
        hideReLoading();
        try {
            // ?????????????????????
            if (getSobotActivity() != null) {
                getSobotActivity().unregisterReceiver(receiver);
                KeyboardUtil.detach(getSobotActivity(), mKPSwitchListener);
            }
            if (localBroadcastManager != null) {
                localBroadcastManager.unregisterReceiver(localReceiver);
            }
        } catch (Exception e) {
            //ignor
        }
        // ???????????????????????????
        stopUserInfoTimeTask();
        // ???????????????????????????
        stopCustomTimeTask();
        stopVoice();
        AudioTools.destory();
        SobotUpload.getInstance().unRegister();
        mPostMsgPresenter.destory();
        if (mEvaluateDialog != null && mEvaluateDialog.isShowing()) {
            mEvaluateDialog.dismiss();
        }
        if (mRobotListDialog != null && mRobotListDialog.isShowing()) {
            mRobotListDialog.dismiss();
        }
        if (SobotOption.sobotViewListener != null) {
            SobotOption.sobotViewListener.onChatActClose(customerState);
        }
        super.onDestroyView();
    }

    private void initView(View rootView) {
        if (rootView == null) {
            return;
        }

        //loading ???
        relative = (RelativeLayout) rootView.findViewById(getResId("sobot_layout_titlebar"));
        sobot_header_center_ll = (LinearLayout) rootView.findViewById(getResId("sobot_header_center_ll"));
        mTitleTextView = (TextView) rootView.findViewById(getResId("sobot_text_title"));
        mAvatarIV = rootView.findViewById(getResId("sobot_avatar_iv"));
        sobot_title_conn_status = (TextView) rootView.findViewById(getResId("sobot_title_conn_status"));
        sobot_container_conn_status = (LinearLayout) rootView.findViewById(getResId("sobot_container_conn_status"));
        sobot_tv_right_second = (TextView) rootView.findViewById(getResId("sobot_tv_right_second"));
        sobot_tv_right_third = (TextView) rootView.findViewById(getResId("sobot_tv_right_third"));
        sobot_conn_loading = (ProgressBar) rootView.findViewById(getResId("sobot_conn_loading"));
        net_status_remide = (RelativeLayout) rootView.findViewById(getResId("sobot_net_status_remide"));
        sobot_net_not_connect = (TextView) rootView.findViewById(getResId("sobot_net_not_connect"));
        sobot_net_not_connect.setText(ResourceUtils.getResString(getSobotActivity(), "sobot_network_unavailable"));

        relative.setVisibility(View.GONE);
        notReadInfo = (TextView) rootView.findViewById(getResId("notReadInfo"));
        chat_main = (RelativeLayout) rootView.findViewById(getResId("sobot_chat_main"));
        welcome = (FrameLayout) rootView.findViewById(getResId("sobot_welcome"));
        txt_loading = (TextView) rootView.findViewById(getResId("sobot_txt_loading"));
        textReConnect = (TextView) rootView.findViewById(getResId("sobot_textReConnect"));
        textReConnect.setText(ResourceUtils.getResString(getSobotActivity(), "sobot_network_unavailable"));
        loading_anim_view = (ProgressBar) rootView.findViewById(getResId("sobot_image_view"));
        image_reLoading = (ImageView) rootView.findViewById(getResId("sobot_image_reloading"));
        icon_nonet = (ImageView) rootView.findViewById(getResId("sobot_icon_nonet"));
        btn_reconnect = (Button) rootView.findViewById(getResId("sobot_btn_reconnect"));
        btn_reconnect.setText(ResourceUtils.getResString(getSobotActivity(), "sobot_reunicon"));
        btn_reconnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                textReConnect.setVisibility(View.GONE);
                icon_nonet.setVisibility(View.GONE);
                btn_reconnect.setVisibility(View.GONE);
                loading_anim_view.setVisibility(View.VISIBLE);
                txt_loading.setVisibility(View.VISIBLE);
                //???????????????????????????????????????
                customerInit(1);
            }
        });

        lv_message = (DropdownListView) rootView.findViewById(getResId("sobot_lv_message"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            lv_message.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        et_sendmessage = (ContainsEmojiEditText) rootView.findViewById(getResId("sobot_et_sendmessage"));
        et_sendmessage.setVisibility(View.VISIBLE);
        btn_send = (Button) rootView.findViewById(getResId("sobot_btn_send"));
        btn_send.setText(ResourceUtils.getResString(getSobotActivity(), "sobot_button_send"));
        btn_set_mode_rengong = (ImageButton) rootView.findViewById(getResId("sobot_btn_set_mode_rengong"));
        view_model_split = rootView.findViewById(getResId("sobot_view_model_split"));
        send_voice_robot_hint = (TextView) rootView.findViewById(getResId("send_voice_robot_hint"));
        send_voice_robot_hint.setHint(ResourceUtils.getResString(getSobotActivity(), "sobot_robot_voice_hint"));
        send_voice_robot_hint.setVisibility(View.GONE);
        btn_upload_view = (Button) rootView.findViewById(getResId("sobot_btn_upload_view"));
        btn_emoticon_view = (ImageButton) rootView.findViewById(getResId("sobot_btn_emoticon_view"));
        btn_model_edit = (ImageButton) rootView.findViewById(getResId("sobot_btn_model_edit"));
        btn_model_voice = (ImageButton) rootView.findViewById(getResId("sobot_btn_model_voice"));
        mPanelRoot = (KPSwitchFSPanelLinearLayout) rootView.findViewById(getResId("sobot_panel_root"));
        btn_press_to_speak = (LinearLayout) rootView.findViewById(getResId("sobot_btn_press_to_speak"));
        edittext_layout = (LinearLayout) rootView.findViewById(getResId("sobot_edittext_layout"));
        recording_hint = (TextView) rootView.findViewById(getResId("sobot_recording_hint"));
        recording_container = (LinearLayout) rootView.findViewById(getResId("sobot_recording_container"));

        // ??????????????????????????????
        voice_top_image = (LinearLayout) rootView.findViewById(getResId("sobot_voice_top_image"));
        // ????????????
        image_endVoice = (ImageView) rootView.findViewById(getResId("sobot_image_endVoice"));
        // ???????????????
        mic_image_animate = (ImageView) rootView.findViewById(getResId("sobot_mic_image_animate"));
        // ???????????????
        voice_time_long = (TextView) rootView.findViewById(getResId("sobot_voiceTimeLong"));
        txt_speak_content = (TextView) rootView.findViewById(getResId("sobot_txt_speak_content"));
        txt_speak_content.setText(getResString("sobot_press_say"));
        recording_timeshort = (ImageView) rootView.findViewById(getResId("sobot_recording_timeshort"));
        mic_image = (ImageView) rootView.findViewById(getResId("sobot_mic_image"));

        sobot_ll_restart_talk = (RelativeLayout) rootView.findViewById(getResId("sobot_ll_restart_talk"));
        sobot_txt_restart_talk = (TextView) rootView.findViewById(getResId("sobot_txt_restart_talk"));
        sobot_txt_restart_talk.setText(ResourceUtils.getResString(getSobotActivity(), "sobot_restart_talk"));
        sobot_tv_message = (TextView) rootView.findViewById(getResId("sobot_tv_message"));
        sobot_tv_message.setText(ResourceUtils.getResString(getSobotActivity(), "sobot_str_bottom_message"));
        sobot_tv_satisfaction = (TextView) rootView.findViewById(getResId("sobot_tv_satisfaction"));
        sobot_tv_satisfaction.setText(ResourceUtils.getResString(getSobotActivity(), "sobot_str_bottom_satisfaction"));
        sobot_ll_bottom = (LinearLayout) rootView.findViewById(getResId("sobot_ll_bottom"));
        sobot_ll_switch_robot = (LinearLayout) rootView.findViewById(getResId("sobot_ll_switch_robot"));
        sobot_tv_switch_robot = (TextView) rootView.findViewById(getResId("sobot_tv_switch_robot"));
        sobot_tv_switch_robot.setText(ResourceUtils.getResString(getSobotActivity(), "sobot_switch_business"));

        sobot_announcement = (RelativeLayout) rootView.findViewById(getResId("sobot_announcement"));
        sobot_announcement_right_icon = (TextView) rootView.findViewById(getResId("sobot_announcement_right_icon"));
        sobot_announcement_title = (TextView) rootView.findViewById(getResId("sobot_announcement_title"));
        sobot_announcement_title.setSelected(true);

        sobot_custom_menu = (HorizontalScrollView) rootView.findViewById(getResId("sobot_custom_menu"));
        sobot_custom_menu.setVisibility(View.GONE);
        sobot_custom_menu_linearlayout = (LinearLayout) rootView.findViewById(getResId("sobot_custom_menu_linearlayout"));

        applyUIConfig();
        mPostMsgPresenter = StPostMsgPresenter.newInstance(SobotChatFSFragment.this, getContext());
    }

    /* ???????????? */
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {

        @SuppressWarnings("unchecked")
        public void handleMessage(final android.os.Message msg) {
            if (!isActive()) {
                return;
            }
            switch (msg.what) {
                case ZhiChiConstant.hander_send_msg:
                    //??????????????????UI
                    updateUiMessage(messageAdapter, msg);
                    lv_message.setSelection(messageAdapter.getCount());
                    break;
                case ZhiChiConstant.send_message_close:
                    //??????????????????
                    //???????????????????????????
                    if (sobot_tv_close != null && info.isShowCloseBtn() && current_client_model == ZhiChiConstant.client_model_customService) {

                        sobot_tv_close.setVisibility(View.VISIBLE);
                    }
                    break;
                case ZhiChiConstant.hander_update_msg_status:
                    //????????????????????????
                    updateMessageStatus(messageAdapter, msg);
                    break;
                case ZhiChiConstant.update_send_data:
                    ZhiChiMessageBase myMessage = (ZhiChiMessageBase) msg.obj;
                    messageAdapter.updateDataById(myMessage.getId(), myMessage);
                    messageAdapter.notifyDataSetChanged();
                    lv_message.setSelection(messageAdapter.getCount());
                    break;
                case ZhiChiConstant.hander_robot_message:
                    ZhiChiMessageBase zhiChiMessageBasebase = (ZhiChiMessageBase) msg.obj;
                    zhiChiMessageBasebase.setT(Calendar.getInstance().getTime().getTime() + "");
                    if (type == ZhiChiConstant.type_robot_first || type == ZhiChiConstant.type_custom_first) {
                        //????????????????????????????????????????????????????????????????????????????????????????????????
                        if (initModel != null && ChatUtils.checkManualType(initModel.getManualType(),
                                zhiChiMessageBasebase.getAnswerType())) {
                            //????????????????????????????????? ???????????????????????????
                            zhiChiMessageBasebase.setShowTransferBtn(true);
                        }
                    }

                    // 1 ???????????????2 ???????????????3 ????????????, 4???????????????5??????????????????6??????????????????
                    // 7 ??????????????????????????????????????????????????????,8??????, 9 ????????????,10 ????????????
                    //??????app ???????????? ??????????????????????????? ?????????????????????1???2, 9???11???12???14?????? 152 ?????????????????????6569??????
                    if (ZhiChiConstant.type_answer_direct.equals(zhiChiMessageBasebase.getAnswerType())
                            || ZhiChiConstant.type_answer_wizard.equals(zhiChiMessageBasebase.getAnswerType())
                            || "2".equals(zhiChiMessageBasebase.getAnswerType())
                            || "11".equals(zhiChiMessageBasebase.getAnswerType())
                            || "12".equals(zhiChiMessageBasebase.getAnswerType())
                            || "14".equals(zhiChiMessageBasebase.getAnswerType()) || (!TextUtils.isEmpty(zhiChiMessageBasebase.getAnswerType()) && zhiChiMessageBasebase.getAnswerType().startsWith("152"))) {
                        if (initModel != null && initModel.isRealuateFlag()) {
                            //?????????????????? ??????????????????
                            zhiChiMessageBasebase.setRevaluateState(1);
                        } else {
                            //?????????????????? ??????????????????
                            zhiChiMessageBasebase.setRevaluateState(0);
                        }
                    }

                    if (zhiChiMessageBasebase.getAnswer() != null && zhiChiMessageBasebase.getAnswer().getMultiDiaRespInfo() != null
                            && zhiChiMessageBasebase.getAnswer().getMultiDiaRespInfo().getEndFlag()) {
                        // ????????????????????????????????????????????????????????????
                        restMultiMsg();
                    }
                    SobotKeyWordTransfer keyWordTransfer = zhiChiMessageBasebase.getSobotKeyWordTransfer();
                    if (keyWordTransfer != null) {
                        //??????????????????
                        if (type != ZhiChiConstant.type_robot_only) {
                            if (1 == keyWordTransfer.getTransferFlag()) {
//                                transferFlag=1???3???
//                                queueFlag=1:??????????????????????????????????????????????????????????????????
//                                        queueFlag=0:
//                                onlineFlag:1 ??????????????????????????????????????????????????????????????????????????????????????????????????????
//                                onlineFlag:2 ?????????????????????????????????????????????????????????????????????????????????????????????????????????
//                                onlineFlag:3 ????????????????????? ????????????????????????????????????????????????
                                if (keyWordTransfer.isQueueFlag()) {
                                    //??????????????????????????????????????????????????????????????????
                                    addKeyWordTipMsg(keyWordTransfer);
                                    transfer2Custom(keyWordTransfer.getGroupId(), keyWordTransfer.getKeyword(), keyWordTransfer.getKeywordId(), keyWordTransfer.isQueueFlag());
                                } else {
                                    if (keyWordTransfer.getOnlineFlag() == 1) {
                                        //??????????????????????????????????????????????????????????????????????????????????????????????????????
                                        addKeyWordTipMsg(keyWordTransfer);
                                        transfer2Custom(keyWordTransfer.getGroupId(), keyWordTransfer.getKeyword(), keyWordTransfer.getKeywordId(), keyWordTransfer.isQueueFlag());
                                    } else if (keyWordTransfer.getOnlineFlag() == 2) {
                                        //?????????????????????????????????????????????????????????????????????????????????????????????????????????
                                        transfer2Custom(keyWordTransfer.getGroupId(), keyWordTransfer.getKeyword(), keyWordTransfer.getKeywordId(), keyWordTransfer.isQueueFlag());
                                    } else if (keyWordTransfer.getOnlineFlag() == 3) {
                                        //????????????????????? ????????????????????????????????????????????????
                                        messageAdapter.justAddData(zhiChiMessageBasebase);
                                    }
                                }
                            } else if (2 == keyWordTransfer.getTransferFlag()) {
                                //??????????????????????????????????????????????????????
                                //????????????????????????????????????cell????????????????????????
                                ZhiChiMessageBase keyWordBase = new ZhiChiMessageBase();
                                keyWordBase.setSenderFace(zhiChiMessageBasebase.getSenderFace());
                                keyWordBase.setSenderType(ZhiChiConstant.message_sender_type_robot_keyword_msg + "");
                                keyWordBase.setSenderName(zhiChiMessageBasebase.getSenderName());
                                keyWordBase.setSobotKeyWordTransfer(keyWordTransfer);
                                messageAdapter.justAddData(keyWordBase);
                            } else if (3 == keyWordTransfer.getTransferFlag()) {
                                if (keyWordTransfer.isQueueFlag()) {
                                    //??????????????????????????????????????????????????????????????????
                                    addKeyWordTipMsg(keyWordTransfer);
                                    //???????????????????????????????????????
                                    transfer2Custom("", "", "", keyWordTransfer.isQueueFlag());
                                } else {
                                    if (keyWordTransfer.getOnlineFlag() == 1) {
                                        //??????????????????????????????????????????????????????????????????????????????????????????????????????
                                        addKeyWordTipMsg(keyWordTransfer);
                                        //???????????????????????????????????????
                                        transfer2Custom("", "", "", keyWordTransfer.isQueueFlag());
                                    } else if (keyWordTransfer.getOnlineFlag() == 2) {
                                        //?????????????????????????????????????????????????????????????????????????????????????????????????????????
                                        //???????????????????????????????????????
                                        transfer2Custom("", "", "", keyWordTransfer.isQueueFlag());
                                    } else if (keyWordTransfer.getOnlineFlag() == 3) {
                                        //????????????????????? ????????????????????????????????????????????????
                                        messageAdapter.justAddData(zhiChiMessageBasebase);
                                    }
                                }
                            }
                        } else {
                            //?????????????????????
                            messageAdapter.justAddData(zhiChiMessageBasebase);
                        }
                    } else {
                        messageAdapter.justAddData(zhiChiMessageBasebase);
                        if (type != ZhiChiConstant.type_robot_only) {
                            //??????????????????????????????
                            if (zhiChiMessageBasebase.getTransferType() == 1
                                    || zhiChiMessageBasebase.getTransferType() == 2 || zhiChiMessageBasebase.getTransferType() == 5) {
                                //??????????????????????????? 5???????????????  ?????????
                                ZhiChiMessageBase robot = ChatUtils.getRobotTransferTip(getContext(), initModel);
                                messageAdapter.justAddData(robot);
                                if (zhiChiMessageBasebase.getTransferType() == 5) {
//                                    6. ????????????????????? 0/1
//                                    7. ????????????????????? 0/1
//                                    8. ?????????????????????0/1
//                                    9. ??????????????? 1
                                    int transferType = 5;
                                    if ("1".equals(zhiChiMessageBasebase.getAnswerType())) {
                                        transferType = 6;
                                    } else if ("2".equals(zhiChiMessageBasebase.getAnswerType())) {
                                        transferType = 7;
                                    } else if ("4".equals(zhiChiMessageBasebase.getAnswerType())) {
                                        transferType = 8;
                                    } else if ("3".equals(zhiChiMessageBasebase.getAnswerType())) {
                                        transferType = 9;
                                    }
                                    transfer2Custom(null, null, null, true, transferType, zhiChiMessageBasebase.getDocId(), zhiChiMessageBasebase.getOriginQuestion(), "0");
                                } else {
                                    transfer2Custom(null, null, null, true, zhiChiMessageBasebase.getTransferType(), zhiChiMessageBasebase.getDocId(), zhiChiMessageBasebase.getOriginQuestion(), "0");
                                }
                            }
                        }
                    }

                    messageAdapter.notifyDataSetChanged();
                    if (SobotMsgManager.getInstance(mAppContext).getConfig(info.getApp_key()).getInitModel() != null) {
                        //???????????????????????????????????? ???????????????view ????????????????????????????????????
                        SobotMsgManager.getInstance(mAppContext).getConfig(info.getApp_key()).addMessage(zhiChiMessageBasebase);
                    }
                    // ?????????????????????????????????????????????????????????????????????????????????????????????
                    if (type == ZhiChiConstant.type_robot_first && (ZhiChiConstant.type_answer_unknown.equals(zhiChiMessageBasebase
                            .getAnswerType()) || ZhiChiConstant.type_answer_guide.equals(zhiChiMessageBasebase
                            .getAnswerType()))) {
                        showTransferCustomer();
                    }
                    gotoLastItem();
                    break;
                // ???????????????????????????
                case ZhiChiConstant.message_type_update_voice:
                    updateVoiceStatusMessage(messageAdapter, msg);
                    break;
                case ZhiChiConstant.message_type_cancel_voice://????????????????????????
                    cancelUiVoiceMessage(messageAdapter, msg);
                    break;
                case ZhiChiConstant.hander_sendPicStatus_success:
                    setTimeTaskMethod(handler);
                    String id = (String) msg.obj;
                    updateUiMessageStatus(messageAdapter, id, ZhiChiConstant.MSG_SEND_STATUS_SUCCESS, 0);
                    break;
                case ZhiChiConstant.hander_sendPicStatus_fail:
                    String resultId = (String) msg.obj;
                    updateUiMessageStatus(messageAdapter, resultId, ZhiChiConstant.MSG_SEND_STATUS_ERROR, 0);
                    break;
                case ZhiChiConstant.hander_sendPicIsLoading:
                    String loadId = (String) msg.obj;
                    int uploadProgress = msg.arg1;
                    updateUiMessageStatus(messageAdapter, loadId, ZhiChiConstant.MSG_SEND_STATUS_LOADING, uploadProgress);
                    break;
                case ZhiChiConstant.hander_timeTask_custom_isBusying: // ?????????????????????
                    // --????????????
                    updateUiMessage(messageAdapter, msg);
                    LogUtils.i("?????????????????????:" + noReplyTimeCustoms);
                    stopCustomTimeTask();
                    break;
                case ZhiChiConstant.hander_timeTask_userInfo:// ?????????????????????
                    updateUiMessage(messageAdapter, msg);
                    stopUserInfoTimeTask();
                    LogUtils.i("??????????????????????????????  ?????????????????????" + noReplyTimeUserInfo);
                    break;
                case ZhiChiConstant.voiceIsRecoding:
                    // ???????????????????????????????????????????????????????????????
                    if (voiceTimerLong >= minRecordTime * 1000) {
                        isCutVoice = true;
                        voiceCuttingMethod();
                        voiceTimerLong = 0;
                        recording_hint.setText(getResString("sobot_voiceTooLong"));
                        recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg"));
                        recording_timeshort.setVisibility(View.VISIBLE);
                        mic_image.setVisibility(View.GONE);
                        mic_image_animate.setVisibility(View.GONE);
                        closeVoiceWindows(2);
                        btn_press_to_speak.setPressed(false);
                        currentVoiceLong = 0;
                    } else {
                        final int time = Integer.parseInt(msg.obj.toString());
//					LogUtils.i("??????????????????????????????" + time);
                        currentVoiceLong = time;
                        if (time < recordDownTime * 1000) {
                            if (time % 1000 == 0) {
                                voiceTimeLongStr = TimeTools.instance.calculatTime(time);
                                voice_time_long.setText(voiceTimeLongStr.substring(3) + "''");
                            }
                        } else if (time < minRecordTime * 1000) {
                            if (time % 1000 == 0) {
                                voiceTimeLongStr = TimeTools.instance.calculatTime(time);
                                voice_time_long.setText(getResString("sobot_count_down") + (minRecordTime * 1000 - time) / 1000);
                            }
                        } else {
                            voice_time_long.setText(getResString("sobot_voiceTooLong"));
                        }
                    }
                    break;
                case ZhiChiConstant.hander_close_voice_view:
                    int longOrShort = msg.arg1;
                    txt_speak_content.setText(getResString("sobot_press_say"));
                    currentVoiceLong = 0;
                    recording_container.setVisibility(View.GONE);

                    if (longOrShort == 0) {
                        for (int i = messageList.size() - 1; i > 0; i--) {
                            if (!TextUtils.isEmpty(messageList.get(i).getSenderType()) &&
                                    Integer.parseInt(messageList.get(i).getSenderType()) == 8) {
                                messageList.remove(i);
                                break;
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    //?????????????????? ?????????????????????????????? 2.9.9??????
    private void addKeyWordTipMsg(SobotKeyWordTransfer keyWordTransfer) {
        if (!TextUtils.isEmpty(keyWordTransfer.getTransferTips())) {
            ZhiChiMessageBase base = new ZhiChiMessageBase();
            base.setT(Calendar.getInstance().getTime().getTime() + "");
            base.setId(System.currentTimeMillis() + "");
            base.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");
            ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
            reply.setRemindType(ZhiChiConstant.sobot_remind_type_simple_tip);
            reply.setMsg(keyWordTransfer.getTransferTips());
            base.setAnswer(reply);
            messageAdapter.justAddData(base);
        }
    }

    protected void initData() {
        setToolBar();
        initBrocastReceiver();
        initListener();
        setupListView();
        loadUnreadNum();
        //???????????????????????????????????????????????????????????????????????????????????????:    ???????????? - ???????????????????????? =?????????
        //??????????????????????????????????????? ??????????????????????????????????????????????????????????????? ???????????????cid ,
        //??????????????????????????????????????????????????????????????????????????????????????????cid????????????
        boolean isReCon = false;
        ZhiChiConfig config = SobotMsgManager.getInstance(mAppContext).getConfig(info.getApp_key());
        if (config != null && config.getInitModel() != null && !config.isAboveZero) {
            long pre_finish_time = SharedPreferencesUtil.getLongData(getSobotActivity(), ZhiChiConstant.SOBOT_FINISH_CURTIME, System.currentTimeMillis());
            long cur_tiem_cha = System.currentTimeMillis() - pre_finish_time;
            if (!TextUtils.isEmpty(config.getInitModel().getUserOutTime()) && pre_finish_time > 0) {
                long userOutTime = Long.parseLong(config.getInitModel().getUserOutTime()) * 60 * 1000;
                isReCon = (cur_tiem_cha - userOutTime) > 0 ? true : false;
                LogUtils.i("?????????????????????????????????????????????????????????" + cur_tiem_cha + " ms");
                LogUtils.i("?????????????????????" + userOutTime + " ms");
                LogUtils.i("??????????????????????????????" + isReCon);
            }
        }
        initSdk(isReCon, 1);
        //??????SobotSessionServer???????????????
        Intent intent = new Intent();
        intent.setAction(ZhiChiConstants.SOBOT_TIMER_BROCAST);
        intent.putExtra("isStartTimer", false);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void setToolBar() {
        if (getView() == null) {
            return;
        }

        if (info != null && info.getTitleImgId() != 0) {
            relative.setBackgroundResource(info.getTitleImgId());
        }
        View rootView = getView();
        View toolBar = rootView.findViewById(getResId("sobot_layout_titlebar"));
        TextView sobot_tv_left = rootView.findViewById(getResId("sobot_tv_left"));
        TextView sobot_tv_right = rootView.findViewById(getResId("sobot_tv_right"));
        sobot_tv_close = rootView.findViewById(getResId("sobot_tv_close"));
        sobot_tv_close.setText(ResourceUtils.getResString(getSobotActivity(), "sobot_colse"));
        if (toolBar != null) {
            if (sobot_tv_left != null) {
                //?????? Toolbar ???????????????,????????????????????????,?????????????????? Activity
                //???????????????????????????
                showLeftMenu(sobot_tv_left, getResDrawableId("sobot_icon_back_grey"), "");
                displayInNotch(sobot_tv_left);
                sobot_tv_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onLeftMenuClick();
                    }
                });
            }

            if (sobot_tv_right != null) {
                if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_moreBtnImgId) {
                    showRightMenu(sobot_tv_right, SobotUIConfig.sobot_moreBtnImgId, "");
                } else {
                    showRightMenu(sobot_tv_right, getResDrawableId("sobot_delete_hismsg_selector"), "");
                }

                sobot_tv_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRightMenuClick(v);
                    }
                });
                if (SobotUIConfig.sobot_title_right_menu1_display) {
                    sobot_tv_right.setVisibility(View.VISIBLE);
                } else {
                    sobot_tv_right.setVisibility(View.GONE);
                }
            }
            if (sobot_tv_close != null && info.isShowCloseBtn() && current_client_model == ZhiChiConstant.client_model_customService) {
                //???????????????????????????
                sobot_tv_close.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initBrocastReceiver() {
        if (receiver == null) {
            receiver = new MyMessageReceiver();
        }
        // ???????????????????????????action????????????????????????action?????????
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); // ?????????????????????
        // ?????????????????????
        getSobotActivity().registerReceiver(receiver, filter);

        if (localReceiver == null) {
            localReceiver = new LocalReceiver();
        }
        localBroadcastManager = LocalBroadcastManager.getInstance(mAppContext);
        // ???????????????????????????action????????????????????????action?????????
        IntentFilter localFilter = new IntentFilter();
        localFilter.addAction(ZhiChiConstants.receiveMessageBrocast);
        localFilter.addAction(ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_LOCATION);
        localFilter.addAction(ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_TEXT);
        localFilter.addAction(ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_OBJECT);
        localFilter.addAction(ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_CARD);
        localFilter.addAction(ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_ORDER_CARD);
        localFilter.addAction(ZhiChiConstant.SOBOT_BROCAST_ACTION_TRASNFER_TO_OPERATOR);
        localFilter.addAction(ZhiChiConstants.chat_remind_post_msg);
        localFilter.addAction(ZhiChiConstants.sobot_click_cancle);
        localFilter.addAction(ZhiChiConstants.SOBOT_POST_MSG_TMP_BROCAST);/*?????????????????????????????????????????????*/
        localFilter.addAction(ZhiChiConstants.dcrc_comment_state);/* ???????????????????????? */
        localFilter.addAction(ZhiChiConstants.sobot_close_now);/* ???????????? */
        localFilter.addAction(ZhiChiConstants.sobot_close_now_clear_cache);// ????????????????????????
        localFilter.addAction(ZhiChiConstants.SOBOT_CHANNEL_STATUS_CHANGE);/* ???????????????????????? */
        localFilter.addAction(ZhiChiConstants.SOBOT_BROCAST_KEYWORD_CLICK);/* ???????????????????????????  ????????????  ?????????  ????????? */
        localFilter.addAction(ZhiChiConstants.SOBOT_BROCAST_REMOVE_FILE_TASK);//??????????????????
        localFilter.addAction(ZhiChiConstants.chat_remind_to_customer);//?????????
        // ?????????????????????
        localBroadcastManager.registerReceiver(localReceiver, localFilter);
    }

    private void initListener() {
        //?????????????????????
        mKPSwitchListener = KeyboardUtil.attach(getSobotActivity(), mPanelRoot,
                new KeyboardUtil.OnKeyboardShowingListener() {
                    @Override
                    public void onKeyboardShowing(boolean isShowing) {
                        resetEmoticonBtn();
                        if (isShowing) {
                            lv_message.setSelection(messageAdapter.getCount());
                        }
                    }
                });
        KPSwitchConflictUtil.attach(mPanelRoot, btn_upload_view, et_sendmessage);

        notReadInfo.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        btn_upload_view.setOnClickListener(this);
        btn_emoticon_view.setOnClickListener(this);
        btn_model_edit.setOnClickListener(this);
        btn_model_voice.setOnClickListener(this);
        sobot_ll_switch_robot.setOnClickListener(this);
        sobot_tv_right_second.setOnClickListener(this);
        sobot_tv_right_third.setOnClickListener(this);


        //????????????????????????????????? ?????????????????????????????????????????????????????????????????????
        boolean is_sobot_use_language = SharedPreferencesUtil.getBooleanData(getSobotActivity(), ZhiChiConstant.SOBOT_USE_LANGUAGE, false);
        if (is_sobot_use_language || !StringUtils.isZh(getSobotActivity())) {
            btn_set_mode_rengong.setBackgroundResource(ResourceUtils.getDrawableId(getSobotActivity(), "sobot_icon_common_manualwork"));
        }
        btn_set_mode_rengong.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                doClickTransferBtn();
            }
        });

        lv_message.setDropdownListScrollListener(new DropdownListView.DropdownListScrollListener() {
            @Override
            public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2, int arg3) {
                if (notReadInfo.getVisibility() == View.VISIBLE && messageList.size() > 0 && messageList.size() > firstVisiableItem) {
                    if (messageList.get(firstVisiableItem) != null && messageList.get(firstVisiableItem).getAnswer() != null
                            && ZhiChiConstant.sobot_remind_type_below_unread == messageList.get(firstVisiableItem).getAnswer().getRemindType()) {
                        notReadInfo.setVisibility(View.GONE);
                    }
                }
            }
        });
        if (sobot_tv_close != null) {
            sobot_tv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCloseMenuClick();
                }
            });
        }
        et_sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                resetBtnUploadAndSend();
            }
        });
        et_sendmessage.setSobotAutoCompleteListener(this);
        et_sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doEmoticonBtn2Blur();
                btn_emoticon_view.setBackgroundResource(ResourceUtils.getDrawableId(getContext(), "sobot_emoticon_button_selector"));
                if (SobotApi.getSwitchMarkStatus(MarkConfig.LANDSCAPE_SCREEN)) {
                    et_sendmessage.dismissPop();
                }

            }
        });
        et_sendmessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean isFocused) {
                if (isFocused) {
                    int length = et_sendmessage.getText().toString().trim().length();
                    if (length != 0) {
                        btn_send.setVisibility(View.VISIBLE);
                        btn_upload_view.setVisibility(View.GONE);
                    }
                    //??????????????????????????????????????????
                    // edittext_layout.setBackgroundResource(getResDrawableId("sobot_chatting_bottom_bg_focus"));
                } else {
                    //  edittext_layout.setBackgroundResource(getResDrawableId("sobot_chatting_bottom_bg_blur"));
                }
            }
        });

        et_sendmessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                resetBtnUploadAndSend();
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        btn_press_to_speak.setOnTouchListener(new PressToSpeakListen());
        lv_message.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    hidePanelAndKeyboard(mPanelRoot);
                }
                return false;
            }
        });

        // ???????????????
        sobot_txt_restart_talk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                initSdk(true, 0);
            }
        });

        sobot_tv_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startToPostMsgActivty(false);
            }
        });

        sobot_tv_satisfaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                submitEvaluation(true, 5, 0, "");
            }
        });
    }

    private void setupListView() {
        messageAdapter = new SobotMsgAdapter(getContext(), messageList, this);
        lv_message.setAdapter(messageAdapter);
        lv_message.setPullRefreshEnable(true);// ????????????????????????
        lv_message.setOnRefreshListenerHead(this);
    }

    /**
     * ????????????????????????
     */
    private void startMicAnimate() {
        mic_image_animate.setBackgroundResource(getResDrawableId("sobot_voice_animation"));
        animationDrawable = (AnimationDrawable) mic_image_animate.getBackground();
        mic_image_animate.post(new Runnable() {
            @Override
            public void run() {
                animationDrawable.start();
            }
        });
        recording_hint.setText(getResString("sobot_move_up_to_cancel"));
        recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg1"));
    }

    public void closeVoiceWindows(int toLongOrShort) {
        Message message = handler.obtainMessage();
        message.what = ZhiChiConstant.hander_close_voice_view;
        message.arg1 = toLongOrShort;
        handler.sendMessageDelayed(message, 500);
    }

    // ???????????????1????????????????????????
    public void voiceCuttingMethod() {
        stopVoice();
        sendVoiceMap(1, voiceMsgId);
        voice_time_long.setText("59" + "''");
    }

    /**
     * ????????????
     */
    private void startVoice() {
        try {
            stopVoice();
            mFileName = SobotPathManager.getInstance().getVoiceDir() + UUID.randomUUID().toString() + ".wav";
            String state = android.os.Environment.getExternalStorageState();
            if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
                LogUtils.i("sd???????????????");
            }
            File directory = new File(mFileName).getParentFile();
            if (!directory.exists() && !directory.mkdirs()) {
                LogUtils.i("?????????????????????");
            }
            extAudioRecorder = ExtAudioRecorder.getInstanse(false);
            extAudioRecorder.setOutputFile(mFileName);
            extAudioRecorder.prepare();
            extAudioRecorder.start(new ExtAudioRecorder.AudioRecorderListener() {
                @Override
                public void onHasPermission() {
                    startMicAnimate();
                    startVoiceTimeTask(handler);
                    sendVoiceMap(0, voiceMsgId);
                }

                @Override
                public void onNoPermission() {
                    ToastUtil.showToast(mAppContext, getResString("sobot_no_record_audio_permission"));
                }
            });
        } catch (Exception e) {
            LogUtils.i("prepare() failed");
        }
    }

    /* ???????????? */
    private void stopVoice() {
        /* ??????????????? */
        try {
            if (extAudioRecorder != null) {
                stopVoiceTimeTask();
                extAudioRecorder.stop();
                extAudioRecorder.release();
            }
        } catch (Exception e) {
        }
    }

    /**
     * ?????????????????????
     */
    public void startVoiceTimeTask(final Handler handler) {
        voiceTimerLong = 0;
        stopVoiceTimeTask();
        voiceTimer = new Timer();
        voiceTimerTask = new TimerTask() {
            @Override
            public void run() {
                // ???????????????:????????????
                sendVoiceTimeTask(handler);
            }
        };
        // 500ms??????????????????
        voiceTimer.schedule(voiceTimerTask, 0, 500);

    }

    /**
     * ??????????????????????????????
     *
     * @param handler
     */
    public void sendVoiceTimeTask(Handler handler) {
        Message message = handler.obtainMessage();
        message.what = ZhiChiConstant.voiceIsRecoding;
        voiceTimerLong = voiceTimerLong + 500;
        message.obj = voiceTimerLong;
        handler.sendMessage(message);
    }

    public void stopVoiceTimeTask() {
        if (voiceTimer != null) {
            voiceTimer.cancel();
            voiceTimer = null;
        }
        if (voiceTimerTask != null) {
            voiceTimerTask.cancel();
            voiceTimerTask = null;
        }
        voiceTimerLong = 0;
    }

    /**
     * ?????????????????????
     *
     * @param type       0????????????????????????  1??????????????????2????????????????????????????????????
     * @param voiceMsgId ????????????ID
     */
    private void sendVoiceMap(int type, String voiceMsgId) {
        // ?????????????????????
        if (type == 0) {
            sendVoiceMessageToHandler(voiceMsgId, mFileName, voiceTimeLongStr, ZhiChiConstant.MSG_SEND_STATUS_ANIM, SEND_VOICE, handler);
        } else if (type == 2) {
            sendVoiceMessageToHandler(voiceMsgId, mFileName, voiceTimeLongStr, ZhiChiConstant.MSG_SEND_STATUS_ERROR, CANCEL_VOICE, handler);
        } else {
            sendVoiceMessageToHandler(voiceMsgId, mFileName, voiceTimeLongStr, ZhiChiConstant.MSG_SEND_STATUS_LOADING, UPDATE_VOICE, handler);
            // ??????http ???????????????????????????
            sendVoice(voiceMsgId, voiceTimeLongStr, initModel.getCid(), initModel.getPartnerid(), mFileName, handler);
            lv_message.setSelection(messageAdapter.getCount());
        }
        gotoLastItem();
    }

    /**
     * ??????????????????
     */
    private void loadUnreadNum() {
        mUnreadNum = SobotMsgManager.getInstance(mAppContext).getUnreadCount(info.getApp_key(), true, info.getPartnerid());
    }

    /**
     * ?????????sdk
     *
     * @param isReConnect ?????????????????????
     **/
    private void initSdk(boolean isReConnect, int isFirstEntry) {
        if (isReConnect) {
            current_client_model = ZhiChiConstant.client_model_robot;
            showTimeVisiableCustomBtn = 0;
            messageList.clear();
            messageAdapter.notifyDataSetChanged();
            cids.clear();
            currentCidPosition = 0;
            queryCidsStatus = ZhiChiConstant.QUERY_CIDS_STATUS_INITIAL;
            isNoMoreHistoryMsg = false;
            isAboveZero = false;
            isComment = false;// ????????????????????? ????????? ???????????????
            customerState = CustomerState.Offline;
            remindRobotMessageTimes = 0;
            queueTimes = 0;
            isSessionOver = false;
            isHasRequestQueryFrom = false;

            sobot_txt_restart_talk.setVisibility(View.GONE);
            sobot_tv_message.setVisibility(View.GONE);
            sobot_tv_satisfaction.setVisibility(View.GONE);
            image_reLoading.setVisibility(View.VISIBLE);
            AnimationUtil.rotate(image_reLoading);

            lv_message.setPullRefreshEnable(true);// ????????????????????????

            String last_current_dreceptionistId = SharedPreferencesUtil.getStringData(
                    mAppContext, info.getApp_key() + "_" + ZhiChiConstant.SOBOT_RECEPTIONISTID, "");
            info.setChoose_adminid(last_current_dreceptionistId);
            resetUser(isFirstEntry);
        } else {
            //?????????????????????????????????
            if (ChatUtils.checkConfigChange(mAppContext, info.getApp_key(), info)) {
                resetUser(isFirstEntry);
            } else {
                doKeepsessionInit(isFirstEntry);
            }
        }
        resetBtnUploadAndSend();
    }

    /**
     * ????????????
     */
    private void resetUser(int isFirstEntry) {
        String platformID = SharedPreferencesUtil.getStringData(mAppContext, ZhiChiConstant.SOBOT_PLATFORM_UNIONCODE, "");
        //???????????????fasle ?????? platformUnionCode ?????????????????????????????????????????????????????????
        if (!SobotVerControl.isPlatformVer || TextUtils.isEmpty(platformID)) {
            zhiChiApi.disconnChannel();
        }
        clearCache();
        SharedPreferencesUtil.saveStringData(mAppContext,
                info.getApp_key() + "_" + ZhiChiConstant.sobot_last_login_group_id, TextUtils.isEmpty(info.getGroupid()) ? "" : info.getGroupid());
        customerInit(isFirstEntry);
    }

    /**
     * ?????????????????????
     */
    private void customerInit(int isFirstEntry) {
        LogUtils.i("customerInit???????????????");
        if (info.getService_mode() == ZhiChiConstant.type_robot_only) {
            ChatUtils.userLogout(mAppContext);
        }
        if (!isAppInitEnd) {
            LogUtils.i("???????????????appinit ????????????????????????????????????????????????");
            return;
        }
        isAppInitEnd = false;
        if (info != null) {
            info.setIsFirstEntry(isFirstEntry);
        }

        //??????ZCSobotApi??????????????????????????????info????????????????????????ZCSobotApi????????????info
        String robot_Hello_Word = SharedPreferencesUtil.getStringData(getSobotActivity(), ZhiChiConstant.SOBOT_ROBOT_HELLO_WORD, "");
        String user_Out_Word = SharedPreferencesUtil.getStringData(getSobotActivity(), ZhiChiConstant.SOBOT_USER_OUT_WORD, "");
        String user_Tip_Word = SharedPreferencesUtil.getStringData(getSobotActivity(), ZhiChiConstant.SOBOT_USER_TIP_WORD, "");
        String admin_Hello_Word = SharedPreferencesUtil.getStringData(getSobotActivity(), ZhiChiConstant.SOBOT_ADMIN_HELLO_WORD, "");
        String admin_Offline_Title = SharedPreferencesUtil.getStringData(getSobotActivity(), ZhiChiConstant.SOBOT_ADMIN_OFFLINE_TITLE, "");
        String admin_Tip_Word = SharedPreferencesUtil.getStringData(getSobotActivity(), ZhiChiConstant.SOBOT_ADMIN_TIP_WORD, "");

        if (info != null) {
            if (TextUtils.isEmpty(info.getRobot_hello_word()) && !TextUtils.isEmpty(robot_Hello_Word)) {
                info.setRobot_Hello_Word(robot_Hello_Word);
            }
            if (TextUtils.isEmpty(info.getUser_out_word()) && !TextUtils.isEmpty(user_Out_Word)) {
                info.setUser_Out_Word(user_Out_Word);
            }
            if (TextUtils.isEmpty(info.getUser_tip_word()) && !TextUtils.isEmpty(user_Tip_Word)) {
                info.setUser_Tip_Word(user_Tip_Word);
            }
            if (TextUtils.isEmpty(info.getAdmin_hello_word()) && !TextUtils.isEmpty(admin_Hello_Word)) {
                info.setAdmin_Hello_Word(admin_Hello_Word);
            }
            if (TextUtils.isEmpty(info.getAdmin_offline_title()) && !TextUtils.isEmpty(admin_Offline_Title)) {
                info.setAdmin_Offline_Title(admin_Offline_Title);
            }
            if (TextUtils.isEmpty(info.getAdmin_tip_word()) && !TextUtils.isEmpty(admin_Tip_Word)) {
                info.setAdmin_Tip_Word(admin_Tip_Word);
            }
        }

        zhiChiApi.sobotInit(SobotChatFSFragment.this, info, new StringResultCallBack<ZhiChiInitModeBase>() {
            @Override
            public void onSuccess(ZhiChiInitModeBase result) {
                isAppInitEnd = true;
                if (!isActive()) {
                    return;
                }
                SharedPreferencesUtil.saveStringData(getSobotActivity(), ZhiChiConstant.sobot_connect_group_id, "");
                SharedPreferencesUtil.saveLongData(getSobotActivity(), ZhiChiConstant.SOBOT_FINISH_CURTIME, 0);
                initModel = result;

                processPlatformAppId();
                getAnnouncement();
                if (info.getService_mode() > 0) {
                    initModel.setType(info.getService_mode() + "");
                }
                type = Integer.parseInt(initModel.getType());
                SharedPreferencesUtil.saveIntData(mAppContext,
                        info.getApp_key() + "_" + ZhiChiConstant.initType, type);
                //???????????????cid
                queryCids();

                //?????????????????????
                sobotCustomMenu();

                //????????????layout,???????????????????????????????????????UI????????????
                showRobotLayout();

                if (!TextUtils.isEmpty(initModel.getPartnerid())) {
                    SharedPreferencesUtil.saveStringData(mAppContext, Const.SOBOT_UID, initModel.getPartnerid());
                }
                if (!TextUtils.isEmpty(initModel.getCid())) {
                    SharedPreferencesUtil.saveStringData(mAppContext, Const.SOBOT_CID, initModel.getCid());
                }
                SharedPreferencesUtil.saveIntData(mAppContext,
                        ZhiChiConstant.sobot_msg_flag, initModel.getMsgFlag());
                SharedPreferencesUtil.saveBooleanData(mAppContext,
                        ZhiChiConstant.sobot_leave_msg_flag, initModel.isMsgToTicketFlag());
                SharedPreferencesUtil.saveStringData(mAppContext,
                        "lastCid", initModel.getCid());
                SharedPreferencesUtil.saveStringData(mAppContext,
                        info.getApp_key() + "_" + ZhiChiConstant.sobot_last_current_partnerId, info.getPartnerid());
                SharedPreferencesUtil.saveOnlyStringData(mAppContext,
                        ZhiChiConstant.sobot_last_current_appkey, info.getApp_key());
                SharedPreferencesUtil.saveObject(mAppContext,
                        ZhiChiConstant.sobot_last_current_info, info);
                SharedPreferencesUtil.saveObject(mAppContext,
                        ZhiChiConstant.sobot_last_current_initModel, initModel);

                SharedPreferencesUtil.saveStringData(mAppContext, info.getApp_key() + "_" + ZhiChiConstant.SOBOT_RECEPTIONISTID, TextUtils.isEmpty(info.getChoose_adminid()) ? "" : info.getChoose_adminid());
                SharedPreferencesUtil.saveStringData(mAppContext, info.getApp_key() + "_" + ZhiChiConstant.SOBOT_ROBOT_CODE, TextUtils.isEmpty(info.getRobotCode()) ? "" : info.getRobotCode());
                SharedPreferencesUtil.saveStringData(mAppContext, info.getApp_key() + "_" + ZhiChiConstant.sobot_last_current_remark, TextUtils.isEmpty(info.getRemark()) ? "" : info.getRemark());
                SharedPreferencesUtil.saveStringData(mAppContext, info.getApp_key() + "_" + ZhiChiConstant.sobot_last_current_groupid, TextUtils.isEmpty(info.getGroupid()) ? "" : info.getGroupid());
                SharedPreferencesUtil.saveIntData(mAppContext, info.getApp_key() + "_" + ZhiChiConstant.sobot_last_current_service_mode, info.getService_mode());
                SharedPreferencesUtil.saveStringData(mAppContext, info.getApp_key() + "_" + ZhiChiConstant.sobot_last_current_customer_fields, TextUtils.isEmpty(info.getCustomer_fields()) ? "" : info.getCustomer_fields());
                SharedPreferencesUtil.saveStringData(mAppContext, info.getApp_key() + "_" + ZhiChiConstant.sobot_last_current_isvip, TextUtils.isEmpty(info.getIsVip()) ? "" : info.getIsVip());
                SharedPreferencesUtil.saveStringData(mAppContext, info.getApp_key() + "_" + ZhiChiConstant.sobot_last_current_vip_level, TextUtils.isEmpty(info.getVip_level()) ? "" : info.getVip_level());
                SharedPreferencesUtil.saveStringData(mAppContext, info.getApp_key() + "_" + ZhiChiConstant.sobot_last_current_user_label, TextUtils.isEmpty(info.getUser_label()) ? "" : info.getUser_label());
                SharedPreferencesUtil.saveStringData(mAppContext, info.getApp_key() + "_" + ZhiChiConstant.sobot_last_current_robot_alias, TextUtils.isEmpty(info.getRobot_alias()) ? "" : info.getRobot_alias());

                // ???????????????
                if (initModel.getAnnounceMsgFlag() && !initModel.isAnnounceTopFlag() && !TextUtils.isEmpty(initModel.getAnnounceMsg())) {
                    ZhiChiMessageBase noticeModel = ChatUtils.getNoticeModel(getContext(), initModel);
                    messageAdapter.justAddData(noticeModel);
                    messageAdapter.notifyDataSetChanged();
                }

                //???????????????????????????????????????????????????????????????id????????????????????????
                if (initModel.getOfflineMsgConnectFlag() == 1 && !TextUtils.isEmpty(initModel.getOfflineMsgAdminId())
                        && !"null".equals(initModel.getOfflineMsgAdminId())) {
                    offlineMsgConnectFlag = initModel.getOfflineMsgConnectFlag();
                    offlineMsgAdminId = initModel.getOfflineMsgAdminId();
                    SobotConnCusParam param = new SobotConnCusParam();
                    connectCustomerService(null, false);
                    return;
                }

                if (type == ZhiChiConstant.type_robot_only) {
                    remindRobotMessage(handler, initModel, info);
                    showSwitchRobotBtn();
                    if (SobotOption.sobotChatStatusListener != null) {
                        //????????????????????????????????????
                        SobotOption.sobotChatStatusListener.onChatStatusListener(SobotChatStatusMode.ZCServerConnectRobot);
                    }
                } else if (type == ZhiChiConstant.type_robot_first) {
                    //???????????????
                    if (initModel.getUstatus() == ZhiChiConstant.ustatus_online || initModel.getUstatus() == ZhiChiConstant.ustatus_queue) {
                        //??????????????? ???????????????  ????????????????????????
                        if (initModel.getUstatus() == ZhiChiConstant.ustatus_queue) {
                            remindRobotMessage(handler, initModel, info);
                        }
                        //?????????????????????
                        connectCustomerService(null);
                    } else {
                        //?????????????????????????????????????????????????????????
                        remindRobotMessage(handler, initModel, info);
                        showSwitchRobotBtn();
                        if (SobotOption.sobotChatStatusListener != null) {
                            //????????????????????????????????????
                            SobotOption.sobotChatStatusListener.onChatStatusListener(SobotChatStatusMode.ZCServerConnectRobot);
                        }
                    }
                } else {
                    if (type == ZhiChiConstant.type_custom_only) {
                        //???????????????
                        if (isUserBlack()) {
                            showLeaveMsg();
                        } else {
                            if (initModel.getInvalidSessionFlag() == 1) {
                                String adminHelloWord = ZCSobotApi.getCurrentInfoSetting(mAppContext) != null ? ZCSobotApi.getCurrentInfoSetting(mAppContext).getAdmin_hello_word() : "";
                                //?????????????????????
                                if (!TextUtils.isEmpty(adminHelloWord)) {
                                    messageAdapter.addData(ChatUtils.getServiceHelloTip("", "", adminHelloWord));
                                } else {
                                    messageAdapter.addData(ChatUtils.getServiceHelloTip("", "", initModel.getAdminHelloWord()));
                                }
                                //??????????????????
                                setBottomView(ZhiChiConstant.bottomViewtype_onlyrobot);
                                btn_set_mode_rengong.setVisibility(View.GONE);
                                btn_model_edit.setVisibility(View.GONE);
                                btn_model_voice.setVisibility(View.GONE);
                                btn_emoticon_view.setVisibility(View.VISIBLE);
                                setAvatar(getResDrawableId("sobot_def_admin"), true);
                                setTitle("", false);
                            } else {
                                transfer2Custom(null, null, null, true, "1");
                            }
                        }
                    } else if (type == ZhiChiConstant.type_custom_first) {
                        if (initModel.getInvalidSessionFlag() == 1) {
                            String rebotHelloWord = ZCSobotApi.getCurrentInfoSetting(mAppContext) != null ? ZCSobotApi.getCurrentInfoSetting(mAppContext).getRobot_hello_word() : "";
                            //????????????????????????
                            if (!TextUtils.isEmpty(rebotHelloWord)) {
                                messageAdapter.addData(ChatUtils.getServiceHelloTip("", "", rebotHelloWord));
                            } else {
                                messageAdapter.addData(ChatUtils.getServiceHelloTip("", "", initModel.getRobotHelloWord()));
                            }
                            //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                            if (info.getAutoSendMsgMode() != null && info.getAutoSendMsgMode() != SobotAutoSendMsgMode.Default) {
                                doClickTransferBtn();
                            }
                        } else {
                            //????????????
                            showSwitchRobotBtn();
                            transfer2Custom(null, null, null, true, "1");
                        }
                    }
                }
                isSessionOver = false;
                //????????????????????????????????????????????????
                if (sobot_tv_close != null) {
                    if (info.isShowCloseBtn() && current_client_model == ZhiChiConstant.client_model_customService) {
                        //??????????????????????????????
                        sobot_tv_close.setVisibility(View.VISIBLE);
                    } else {
                        sobot_tv_close.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                isAppInitEnd = true;
                SharedPreferencesUtil.saveObject(mAppContext,
                        ZhiChiConstant.sobot_last_current_info, info);
                if (!isActive()) {
                    return;
                }
                if (e instanceof IllegalArgumentException && !TextUtils.isEmpty(des)) {
                    ToastUtil.showToast(mAppContext, des);
                    finish();
                } else {
                    showInitError();
                }
                isSessionOver = true;
            }
        });
    }

    /**
     * ????????????????????????CustomerCode?????????
     */
    private void processPlatformAppId() {
        if (SobotVerControl.isPlatformVer && !TextUtils.isEmpty(info.getCustomer_code())) {
            if (!TextUtils.isEmpty(initModel.getAppId())) {
                info.setApp_key(initModel.getAppId());
            }

            SharedPreferencesUtil.saveStringData(mAppContext, ZhiChiConstant.SOBOT_CURRENT_IM_APPID, info.getApp_key());
        }
    }

    /**
     * ??????????????????????????????
     */
    private void doKeepsessionInit(int isFirstEntry) {
        List<ZhiChiMessageBase> tmpList = SobotMsgManager.getInstance(mAppContext).getConfig(info.getApp_key()).getMessageList();
        if (tmpList != null && SobotMsgManager.getInstance(mAppContext).getConfig(info.getApp_key()).getInitModel() != null) {
            //?????????
            int lastType = SharedPreferencesUtil.getIntData(mAppContext,
                    info.getApp_key() + "_" + ZhiChiConstant.initType, -1);
            if (info.getService_mode() < 0 || lastType == info.getService_mode()) {
                if (!TextUtils.isEmpty(info.getGroupid())) {
                    //?????????????????????????????????
                    String lastUseGroupId = SharedPreferencesUtil.getStringData(mAppContext, info.getApp_key() + "_" + ZhiChiConstant.sobot_last_login_group_id, "");
                    if (lastUseGroupId.equals(info.getGroupid())) {
                        keepSession(tmpList);
                    } else {
                        resetUser(isFirstEntry);
                    }
                } else {
                    keepSession(tmpList);
                }
            } else {
                resetUser(isFirstEntry);
            }
        } else {
            resetUser(isFirstEntry);
        }
    }

    /**
     * ?????????????????????
     *
     * @param initModel
     * @param outLineType ???????????????
     */
    public void customerServiceOffline(ZhiChiInitModeBase initModel, int outLineType) {
        if (initModel == null) {
            return;
        }
        queueNum = 0;
        stopInputListener();
        stopUserInfoTimeTask();
        stopCustomTimeTask();
        customerState = CustomerState.Offline;

        // ????????????
        showOutlineTip(initModel, outLineType);
        //??????????????????
        setBottomView(ZhiChiConstant.bottomViewtype_outline);
        //????????????????????????
        sobot_custom_menu.setVisibility(View.GONE);
        mBottomViewtype = ZhiChiConstant.bottomViewtype_outline;

        if (Integer.parseInt(initModel.getType()) == ZhiChiConstant.type_custom_only) {
            if (1 == outLineType) {
                //?????????????????? ????????????????????????????????????
                showLogicTitle(getResString("sobot_no_access"), null, false);
            }
        }

        if (6 == outLineType) {
            LogUtils.i("???????????????");
        }
        isSessionOver = true;
        // ???????????????????????????
        CommonUtils.sendLocalBroadcast(mAppContext, new Intent(Const.SOBOT_CHAT_USER_OUTLINE));
        stopPolling();
    }

    /**
     * ??????????????????
     *
     * @param initModel
     * @param outLineType ????????????
     */
    private void showOutlineTip(ZhiChiInitModeBase initModel, int outLineType) {
        if (SobotOption.sobotChatStatusListener != null) {
            //?????????????????????????????????
            SobotOption.sobotChatStatusListener.onChatStatusListener(SobotChatStatusMode.ZCServerConnectOffline);
        }
        String offlineMsg = ChatUtils.getMessageContentByOutLineType(mAppContext, initModel, outLineType);
        if (!TextUtils.isEmpty(offlineMsg)) {
            ZhiChiMessageBase base = new ZhiChiMessageBase();
            base.setT(Calendar.getInstance().getTime().getTime() + "");
            ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
            base.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");
            reply.setRemindType(ZhiChiConstant.sobot_remind_type_outline);
            base.setAnswer(reply);
            if (1 == outLineType) {
                base.setAction(ZhiChiConstant.sobot_outline_leverByManager);
            } else if (2 == outLineType) {
                offlineMsg = offlineMsg.replace("#" + ResourceUtils.getResString(getContext(), "sobot_cus_service") + "#", currentUserName);
                base.setAction(ZhiChiConstant.sobot_outline_leverByManager);
            } else if (3 == outLineType) {
                base.setAction(ZhiChiConstant.sobot_outline_leverByManager);
                if (initModel != null) {
                    initModel.setIsblack("1");
                }
            } else if (5 == outLineType) {
                base.setAction(ZhiChiConstant.sobot_outline_leverByManager);
            } else if (4 == outLineType) {
                base.setAction(ZhiChiConstant.action_remind_past_time);
            } else if (6 == outLineType) {
                base.setAction(ZhiChiConstant.sobot_outline_leverByManager);
            } else if (99 == outLineType) {
                //????????????????????? ???????????????????????????????????????
                base.setAction(ZhiChiConstant.sobot_outline_leverByManager);
            }
            reply.setMsg(offlineMsg);
            // ??????????????????
            updateUiMessage(messageAdapter, base);
        }
    }

    /**
     * ??????????????????
     */
    private void showInLineHint(String queueDoc) {
        // ?????????????????????
        if (!TextUtils.isEmpty(queueDoc)) {
            updateUiMessage(messageAdapter, ChatUtils.getInLineHint(queueDoc));
            gotoLastItem();
        }
    }

    //????????????
    private void keepSession(List<ZhiChiMessageBase> tmpList) {
        ZhiChiConfig config = SobotMsgManager.getInstance(mAppContext).getConfig(info.getApp_key());
        initModel = config.getInitModel();
        updateFloatUnreadIcon();
        mUnreadNum = 0;
        messageAdapter.addData(tmpList);
        messageAdapter.notifyDataSetChanged();
        current_client_model = config.current_client_model;
        type = Integer.parseInt(initModel.getType());

        String currentCid = initModel.getCid();
        if (preCurrentCid == null) {
            statusFlag = 0;
        } else if (!currentCid.equals(preCurrentCid)) {
            statusFlag = 0;
        }
        SharedPreferencesUtil.saveIntData(mAppContext,
                info.getApp_key() + "_" + ZhiChiConstant.initType, type);
        LogUtils.i("sobot----type---->" + type);
        showLogicTitle(config.activityTitle, config.adminFace, false);
        showSwitchRobotBtn();
        customerState = config.customerState;
        remindRobotMessageTimes = config.remindRobotMessageTimes;
        isComment = config.isComment;
        isAboveZero = config.isAboveZero;
        currentUserName = config.currentUserName;
        isNoMoreHistoryMsg = config.isNoMoreHistoryMsg;
        currentCidPosition = config.currentCidPosition;
        queryCidsStatus = config.queryCidsStatus;
        isShowQueueTip = config.isShowQueueTip;
        if (config.cids != null) {
            cids.addAll(config.cids);
        }
        showTimeVisiableCustomBtn = config.showTimeVisiableCustomBtn;
        queueNum = config.queueNum;
        if (isNoMoreHistoryMsg) {
            lv_message.setPullRefreshEnable(false);// ????????????????????????
        }
        setAdminFace(config.adminFace);
        mBottomViewtype = config.bottomViewtype;
        setBottomView(config.bottomViewtype);
        isChatLock = config.isChatLock;
        if (type == ZhiChiConstant.type_custom_only && statusFlag == 0) {
            //???????????????
            preCurrentCid = currentCid;
            if (isUserBlack()) {
                showLeaveMsg();
            } else {
                if (initModel.getInvalidSessionFlag() == 1) {
                    //??????????????????
                    setBottomView(ZhiChiConstant.bottomViewtype_onlyrobot);
                    btn_set_mode_rengong.setVisibility(View.GONE);
                    btn_model_edit.setVisibility(View.GONE);
                    btn_model_voice.setVisibility(View.GONE);
                    btn_emoticon_view.setVisibility(View.VISIBLE);
                    tempMsgContent = config.tempMsgContent;
                    setAvatar(getResDrawableId("sobot_def_admin"), true);
                    setTitle("", false);
                } else {
                    transfer2Custom(null, null, null, true, "1");
                }
            }
        }
        if (type == ZhiChiConstant.type_custom_first && statusFlag == 0) {
            //????????????
            tempMsgContent = config.tempMsgContent;
        }
        LogUtils.i("sobot----isChatLock--->" + "userInfoTimeTask " + config.userInfoTimeTask + "=====customTimeTask====" + config.customTimeTask + isChatLock);
        paseReplyTimeCustoms = config.paseReplyTimeCustoms;
        paseReplyTimeUserInfo = config.paseReplyTimeUserInfo;
        if (config.userInfoTimeTask && isChatLock != 1) {
            stopUserInfoTimeTask();
            startUserInfoTimeTask(handler);
            noReplyTimeUserInfo = config.paseReplyTimeUserInfo;
        }
        if (config.customTimeTask && isChatLock != 1) {
            stopCustomTimeTask();
            startCustomTimeTask(handler);
            noReplyTimeCustoms = config.paseReplyTimeCustoms;
        }
        if (info.getAutoSendMsgMode().geIsEveryTimeAutoSend()) {
            //????????????
            config.isProcessAutoSendMsg = true;
        }
        if (config.isProcessAutoSendMsg) {
            //?????????????????????
            if (info.getAutoSendMsgMode().getAuto_send_msgtype() == ZCMessageTypeText) {
                //????????????????????????
                processAutoSendMsg(info);
            } else {
                //?????????????????????????????????????????????????????? (????????????????????????)
                if (info.getAutoSendMsgMode() != null && info.getAutoSendMsgMode() != SobotAutoSendMsgMode.Default && current_client_model == ZhiChiConstant.client_model_customService && !TextUtils.isEmpty(info.getAutoSendMsgMode().getContent())) {
                    if (info.getAutoSendMsgMode() == SobotAutoSendMsgMode.SendToOperator && customerState == CustomerState.Online) {
                        //????????????
                        String content = info.getAutoSendMsgMode().getContent();
                        if (info.getAutoSendMsgMode().getAuto_send_msgtype() == ZCMessageTypeFile) {
                            //????????????
                            File sendFile = new File(content);
                            if (sendFile.exists()) {
                                uploadFile(sendFile, handler, lv_message, messageAdapter, false);
                            }
                        } else if (info.getAutoSendMsgMode().getAuto_send_msgtype() == ZCMessageTypeVideo) {
                            //????????????
                            File sendFile = new File(content);
                            if (sendFile.exists()) {
                                uploadVideo(sendFile, null, messageAdapter);
                            }
                        } else if (info.getAutoSendMsgMode().getAuto_send_msgtype() == ZCMessageTypePhoto) {
                            //????????????
                            File sendFile = new File(content);
                            if (sendFile.exists()) {
                                uploadFile(sendFile, handler, lv_message, messageAdapter, false);
                            }
                        }
                    }
                }
            }
            config.isProcessAutoSendMsg = false;
        }
        //????????????????????????
        et_sendmessage.setRequestParams(initModel.getPartnerid(), initModel.getRobotid());
        if (customerState == CustomerState.Online && current_client_model == ZhiChiConstant.client_model_customService) {
            createConsultingContent(1);
            createOrderCardContent(1);
            //????????????????????????????????????
            et_sendmessage.setAutoCompleteEnable(false);
            //??????????????????
            showLogicTitle(null, config.adminFace, false);
        } else {
            //?????????????????????????????????
            et_sendmessage.setAutoCompleteEnable(true);
            //?????????????????????
            showLogicTitle(null, initModel.getRobotLogo(), false);
        }
        lv_message.setSelection(messageAdapter.getCount());
        getAnnouncement();
        sobotCustomMenu();
        config.clearMessageList();
        config.clearInitModel();
        isSessionOver = false;
        for (int i = messageList.size() - 1; i > 0; i--) {
            if (!TextUtils.isEmpty(messageList.get(i).getSenderType()) && Integer.parseInt(messageList.get(i).getSenderType()) == ZhiChiConstant.message_sender_type_remide_info
                    && messageList.get(i).getAnswer() != null
                    && ZhiChiConstant.sobot_remind_type_simple_tip == messageList.get(i).getAnswer().getRemindType()) {
                messageList.remove(i);
                messageAdapter.notifyDataSetChanged();
                break;
            }
        }
        processNewTicketMsg(handler);
        inPolling = config.inPolling;
        //??????????????????????????????????????????????????????????????????
        if (current_client_model == ZhiChiConstant.client_model_customService && inPolling && !CommonUtils.isServiceWork(getSobotActivity(), "com.sobot.chat.core.channel.SobotTCPServer")) {
            startPolling();
        }
    }

    /**
     * ?????????????????????????????????????????????????????????????????????
     */
    private void showTransferCustomer() {
        showTimeVisiableCustomBtn++;
        if (showTimeVisiableCustomBtn >= info.getArtificialIntelligenceNum()) {
            btn_set_mode_rengong.setVisibility(View.VISIBLE);
        }
    }

    /**
     * ??????????????????????????????id ???????????????
     */
    private void transfer2CustomBySkillId(int transferType) {
        SobotConnCusParam param = new SobotConnCusParam();
        param.setGroupId(info.getGroupid());
        param.setGroupName(info.getGroup_name());
        param.setTransferType(transferType);
        requestQueryFrom(param, info.isCloseInquiryForm());
    }

    /**
     * ??????????????????   ????????????????????????????????????????????????
     */
    private void showEmotionBtn() {
        Map<String, String> mapAll = DisplayEmojiRules.getMapAll(mAppContext);
        if (mapAll.size() > 0) {
            btn_emoticon_view.setVisibility(View.VISIBLE);
        } else {
            btn_emoticon_view.setVisibility(View.GONE);
        }
    }

    private void transfer2Custom(String tempGroupId, String keyword, String keywordId, boolean isShowTips) {
        transfer2Custom(tempGroupId, keyword, keywordId, isShowTips, 0, "", "", "0");
    }

    private void transfer2Custom(String tempGroupId, String keyword, String keywordId, boolean isShowTips, String activeTransfer) {
        transfer2Custom(tempGroupId, keyword, keywordId, isShowTips, 0, "", "", activeTransfer);
    }

    private void transfer2Custom(String tempGroupId, String keyword, String keywordId, boolean isShowTips, String docId, String unknownQuestion, String activeTransfer) {
        transfer2Custom(tempGroupId, keyword, keywordId, isShowTips, 0, docId, unknownQuestion, activeTransfer);
    }

    /**
     * ??????????????????????????????
     * ?????????????????????skillId ??????????????????id???????????????
     * ???????????????  ??????????????????????????????????????????
     *
     * @param tempGroupId  ?????????id
     * @param keyword      ???????????????????????????
     * @param keywordId    ???????????????????????????id
     * @param isShowTips   ??????????????????
     * @param transferType ??????????????? ???????????????????????????????????? ????????????????????????
     *                     0?????? 1???????????? 2???????????? ?????????
     */
    private void transfer2Custom(String tempGroupId, String keyword, String keywordId, boolean isShowTips, int transferType, String docId, String unknownQuestion, String activeTransfer) {
        if (isUserBlack()) {
            SobotConnCusParam param = new SobotConnCusParam();
            param.setKeyword(keyword);
            param.setKeywordId(keywordId);
            param.setDocId(docId);
            param.setUnknownQuestion(unknownQuestion);
            param.setActiveTransfer(activeTransfer);
            connectCustomerService(param, isShowTips);
        } else if (SobotOption.transferOperatorInterceptor != null) {
            // ???????????????
            SobotTransferOperatorParam param = new SobotTransferOperatorParam();
            param.setGroupId(tempGroupId);
            param.setKeyword(keyword);
            param.setKeywordId(keywordId);
            param.setShowTips(isShowTips);
            param.setTransferType(transferType);
            param.setConsultingContent(info.getConsultingContent());
            SobotOption.transferOperatorInterceptor.onTransferStart(getContext(), param);
        } else if (!TextUtils.isEmpty(info.getGroupid())) {
            //????????????????????????
            if (!TextUtils.isEmpty(keyword)) {
                SobotConnCusParam param = new SobotConnCusParam();
                param.setGroupId(info.getGroupid());
                param.setGroupName("");
                param.setKeyword(keyword);
                param.setKeywordId(keywordId);
                param.setDocId(docId);
                param.setUnknownQuestion(unknownQuestion);
                param.setActiveTransfer(activeTransfer);
                connectCustomerService(param, isShowTips);
            } else {
                transfer2CustomBySkillId(transferType);
            }

        } else {
            if (!TextUtils.isEmpty(keyword)) {
                SobotConnCusParam param = new SobotConnCusParam();
                param.setGroupId(tempGroupId);
                param.setGroupName("");
                param.setKeyword(keyword);
                param.setKeywordId(keywordId);
                param.setDocId(docId);
                param.setUnknownQuestion(unknownQuestion);
                param.setActiveTransfer(activeTransfer);
                connectCustomerService(param, isShowTips);
            } else {
                if (initModel.getGroupflag().equals(ZhiChiConstant.groupflag_on)
                        && TextUtils.isEmpty(info.getChoose_adminid())
                        && !initModel.isSmartRouteInfoFlag()
                        && TextUtils.isEmpty(info.getTransferAction())) {
                    //??????????????????id  ?????????????????????????????????????????????????????????
                    //??????????????????????????????????????????????????????????????????????????????
                    SobotConnCusParam param = new SobotConnCusParam();
                    param.setTransferType(transferType);
                    param.setDocId(docId);
                    param.setUnknownQuestion(unknownQuestion);
                    param.setActiveTransfer(activeTransfer);
                    getGroupInfo(param);
                } else {
                    //???????????????????????????????????????  ???????????????
                    SobotConnCusParam param = new SobotConnCusParam();
                    param.setTransferType(transferType);
                    param.setDocId(docId);
                    param.setUnknownQuestion(unknownQuestion);
                    param.setActiveTransfer(activeTransfer);
                    requestQueryFrom(param, info.isCloseInquiryForm());
                }
            }

        }
    }

    /**
     * ???????????????
     *
     * @param param ???????????????
     */
    private void getGroupInfo(final SobotConnCusParam param) {
        zhiChiApi.getGroupList(SobotChatFSFragment.this, info.getApp_key(), initModel.getPartnerid(), new StringResultCallBack<ZhiChiGroup>() {
            @Override
            public void onSuccess(ZhiChiGroup zhiChiGroup) {
                if (!isActive()) {
                    return;
                }
                boolean hasOnlineCustom = false;
                if (ZhiChiConstant.groupList_ustatus_time_out.equals(zhiChiGroup.getUstatus())) {
                    customerServiceOffline(initModel, 4);
                } else {
                    list_group = zhiChiGroup.getData();
                    if (list_group != null && list_group.size() > 0) {
                        for (int i = 0; i < list_group.size(); i++) {
                            if ("true".equals(list_group.get(i).isOnline())) {
                                hasOnlineCustom = true;
                                break;
                            }
                        }
                        if (hasOnlineCustom) {
                            if (initModel.getUstatus() == ZhiChiConstant.ustatus_online || initModel.getUstatus() == ZhiChiConstant.ustatus_queue) {
                                // ???????????????????????????
                                connectCustomerService(null);
                            } else {
                                //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                                // ??????????????????????????????????????????;????????????????????????????????????????????????????????????
                                if (!TextUtils.isEmpty(info.getGroupid())) {
                                    //???????????????
                                    transfer2CustomBySkillId(param != null ? param.getTransferType() : 0);
                                } else {
                                    Intent intent = new Intent(mAppContext, SobotSkillGroupActivity.class);
                                    intent.putExtra("grouplist", (Serializable) list_group);
                                    intent.putExtra("uid", initModel.getPartnerid());
                                    intent.putExtra("type", type);
                                    intent.putExtra("appkey", info.getApp_key());
                                    intent.putExtra("companyId", initModel.getCompanyId());
                                    intent.putExtra("msgTmp", initModel.getMsgTmp());
                                    intent.putExtra("msgTxt", initModel.getMsgTxt());
                                    intent.putExtra("msgFlag", initModel.getMsgFlag());
                                    intent.putExtra("transferType", param != null ? param.getTransferType() : 0);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("sobotConnCusParam", param);
                                    intent.putExtras(bundle);
                                    startActivityForResult(intent, ZhiChiConstant.REQUEST_COCE_TO_GRROUP);
                                }
                            }
                        } else {
                            if (messageAdapter != null && keyWordMessageBase != null) {
                                messageAdapter.justAddData(keyWordMessageBase);
                                messageAdapter.notifyDataSetChanged();
                                keyWordMessageBase = null;
                            } else {
                                //???????????????????????????
                                connCustomerServiceFail(true);
                            }
                        }
                    } else {
                        //?????????????????????
                        requestQueryFrom(param, info.isCloseInquiryForm());
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                if (!isActive()) {
                    return;
                }
                ToastUtil.showToast(mAppContext, des);
            }
        });
    }

    /**
     * ???????????????
     */
    private void connCustomerServiceFail(boolean isShowTips) {
        if (type == 2) {
            showLeaveMsg();
        } else {
            showLogicTitle(initModel.getRobotName(), initModel.getRobotLogo(), false);
            showSwitchRobotBtn();
            if (isShowTips) {
                showCustomerOfflineTip();
            }
            if (type == ZhiChiConstant.type_custom_first && current_client_model ==
                    ZhiChiConstant.client_model_robot) {
                remindRobotMessage(handler, initModel, info);
            }
        }
        gotoLastItem();
    }

    /**
     * ????????? ??????????????????
     */
    private void connCustomerServiceBlack(boolean isShowTips) {
        showLogicTitle(initModel.getRobotName(), initModel.getRobotLogo(), false);
        showSwitchRobotBtn();
        if (isShowTips) {
            showCustomerUanbleTip();
        }
        if (type == ZhiChiConstant.type_custom_first) {
            remindRobotMessage(handler, initModel, info);
        }
    }

    /**
     * ????????????????????????
     */
    private void showRobotLayout() {
        if (initModel != null) {
            if (type == 1) {
                //????????????
                setBottomView(ZhiChiConstant.bottomViewtype_onlyrobot);
                mBottomViewtype = ZhiChiConstant.bottomViewtype_onlyrobot;
                showLogicTitle(initModel.getRobotName(), initModel.getRobotLogo(), false);
            } else if (type == 3 || type == 4) {
                //???????????????
                setBottomView(ZhiChiConstant.bottomViewtype_robot);
                mBottomViewtype = ZhiChiConstant.bottomViewtype_robot;
                showLogicTitle(initModel.getRobotName(), initModel.getRobotLogo(), false);
            } else if (type == 2) {
                setBottomView(ZhiChiConstant.bottomViewtype_customer);
                mBottomViewtype = ZhiChiConstant.bottomViewtype_customer;
                showLogicTitle(getResString("sobot_connecting_customer_service"), null, false);
            }
            //???????????????????????????????????????
            if (type != ZhiChiConstant.type_custom_only) {
                //?????????????????????????????????????????????????????????
                et_sendmessage.setRequestParams(initModel.getPartnerid(), initModel.getRobotid());
                et_sendmessage.setAutoCompleteEnable(true);
            }
        }
    }

    /**
     * ???????????????
     *
     * @param param ?????????????????????
     */
    protected void connectCustomerService(SobotConnCusParam param, final boolean isShowTips) {
        if (isConnCustomerService) {
            return;
        }
        isConnCustomerService = true;
        boolean currentFlag = (customerState == CustomerState.Queuing || customerState == CustomerState.Online);

        if (param == null) {
            param = new SobotConnCusParam();
        }
        param.setChooseAdminId(info.getChoose_adminid());
        param.setTran_flag(info.getTranReceptionistFlag());
        param.setPartnerid(initModel.getPartnerid());
        param.setCid(initModel.getCid());
        param.setCurrentFlag(currentFlag);
        param.setTransferAction(info.getTransferAction());
        param.setIs_Queue_First(info.is_queue_first());
        param.setSummary_params(info.getSummary_params());
        param.setOfflineMsgAdminId(offlineMsgAdminId);
        param.setOfflineMsgConnectFlag(offlineMsgConnectFlag);
        SharedPreferencesUtil.saveStringData(getSobotActivity(), ZhiChiConstant.sobot_connect_group_id, param.getGroupId());

//        SobotConnCusParam  param = new SobotConnCusParam();
//        param.setGroupId(groupId);
//        param.setGroupName(groupName);
//        param.setKeyword(keyword);
//        param.setKeywordId(keywordId);
//        param.setTransferType(transferType);
//        param.setDocId(docId);
//        param.setUnknownQuestion(unknownQuestion);
//        param.setActiveTransfer(activeTransfer);

        final String keyword = param.getKeyword();
        final String keywordId = param.getKeywordId();
        final String docId = param.getDocId();
        final String unknownQuestion = param.getUnknownQuestion();
        final String activeTransfer = param.getActiveTransfer();

        zhiChiApi.connnect(SobotChatFSFragment.this, param,
                new StringResultCallBack<ZhiChiMessageBase>() {
                    @Override
                    public void onSuccess(ZhiChiMessageBase zhichiMessageBase) {
                        LogUtils.i("connectCustomerService:zhichiMessageBase= " + zhichiMessageBase);
                        isConnCustomerService = false;
                        offlineMsgAdminId = "";
                        offlineMsgConnectFlag = 0;
                        if (!isActive()) {
                            return;
                        }

                        if (!TextUtils.isEmpty(zhichiMessageBase.getServiceEndPushMsg())) {
                            initModel.setServiceEndPushMsg(zhichiMessageBase.getServiceEndPushMsg());
                        }

                        int status = Integer.parseInt(zhichiMessageBase.getStatus());
                        statusFlag = status;
                        preCurrentCid = initModel.getCid();
                        setAdminFace(zhichiMessageBase.getAface());
                        LogUtils.i("status---:" + status);
                        if (status != 0) {
                            if (status == ZhiChiConstant.transfer_robot_customServeive) {
                                //??????????????????????????????
                                customerServiceOffline(initModel, 4);
                            } else if (status == ZhiChiConstant.transfer_robot_custom_status) {
                                if (TextUtils.isEmpty(keywordId)) {
                                    //???????????????????????????id???????????????????????????????????????????????????status=6.?????????????????????receptionistId???null
                                    //???null?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                                    showLogicTitle(initModel.getRobotName(), initModel.getRobotLogo(), false);
                                    info.setChoose_adminid(null);
                                    //???????????????????????????????????????
                                    initModel.setSmartRouteInfoFlag(false);
                                    transfer2Custom(null, keyword, keywordId, isShowTips, docId, unknownQuestion, activeTransfer);
                                }
                            } else {
                                if (ZhiChiConstant.transfer_customeServeive_success == status) {
                                    connCustomerServiceSuccess(zhichiMessageBase);
                                } else if (ZhiChiConstant.transfer_customeServeive_fail == status) {
                                    if (messageAdapter != null && keyWordMessageBase != null) {
                                        messageAdapter.justAddData(keyWordMessageBase);
                                        messageAdapter.notifyDataSetChanged();
                                        keyWordMessageBase = null;
                                    } else {
                                        connCustomerServiceFail(isShowTips);
                                    }
                                } else if (ZhiChiConstant.transfer_customeServeive_isBalk == status) {
                                    if (messageAdapter != null && keyWordMessageBase != null) {
                                        messageAdapter.justAddData(keyWordMessageBase);
                                        messageAdapter.notifyDataSetChanged();
                                        keyWordMessageBase = null;
                                    } else {
                                        connCustomerServiceBlack(isShowTips);
                                    }
                                } else if (ZhiChiConstant.transfer_customeServeive_already == status) {
                                    connCustomerServiceSuccess(zhichiMessageBase);
                                } else if (ZhiChiConstant.transfer_robot_custom_max_status == status) {
                                    if (type == 2) {
                                        showLogicTitle(getResString("sobot_wait_full"), null, true);
                                        setBottomView(ZhiChiConstant.bottomViewtype_custom_only_msgclose);
                                        mBottomViewtype = ZhiChiConstant.bottomViewtype_custom_only_msgclose;
                                    }

                                    if (initModel.getMsgFlag() == ZhiChiConstant.sobot_msg_flag_open) {
                                        if (!TextUtils.isEmpty(zhichiMessageBase.getMsg())) {
                                            ToastUtil.showCustomToastWithListenr(mAppContext, zhichiMessageBase.getMsg(), 3000, new ToastUtil.OnAfterShowListener() {
                                                @Override
                                                public void doAfter() {
                                                    startToPostMsgActivty(false);
                                                }
                                            });
                                        } else {
                                            ToastUtil.showCustomToastWithListenr(mAppContext, ResourceUtils.getResString(mAppContext, "sobot_line_transfinite_def_hint"), 3000, new ToastUtil.OnAfterShowListener() {
                                                @Override
                                                public void doAfter() {
                                                    startToPostMsgActivty(false);
                                                }
                                            });
                                        }

                                    }
                                    showSwitchRobotBtn();
                                }
                            }
                        } else {
                            LogUtils.i("?????????--??????");
                            //????????????
                            zhiChiApi.connChannel(zhichiMessageBase.getWslinkBak(),
                                    zhichiMessageBase.getWslinkDefault(), initModel.getPartnerid(), zhichiMessageBase.getPuid(), info.getApp_key(), zhichiMessageBase.getWayHttp());
                            customerState = CustomerState.Queuing;
                            isShowQueueTip = isShowTips;
                            createCustomerQueue(zhichiMessageBase.getCount() + "", status, zhichiMessageBase.getQueueDoc(), isShowTips);
                        }
                    }

                    @Override
                    public void onFailure(Exception e, String des) {
                        LogUtils.i("connectCustomerService:e= " + e.toString() + des);
                        isConnCustomerService = false;
                        if (messageAdapter != null && keyWordMessageBase != null) {
                            messageAdapter.justAddData(keyWordMessageBase);
                            keyWordMessageBase = null;
                        }
                        if (!isActive()) {
                            return;
                        }
                        if (type == 2) {
                            setBottomView(ZhiChiConstant.bottomViewtype_custom_only_msgclose);
                            showLogicTitle(getResString("sobot_no_access"), null, false);
                            isSessionOver = true;
                        }
                        ToastUtil.showToast(mAppContext, des);
                    }
                });
    }

    private void gotoLastItem() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                lv_message.setSelection(messageAdapter.getCount());
            }
        });
    }

    /**
     * ????????????????????????????????????UI  ???XX??????????????????
     */
    private void updateFloatUnreadIcon() {
        if (mUnreadNum >= 10) {
            notReadInfo.setVisibility(View.VISIBLE);
            notReadInfo.setText(mUnreadNum + getResString("sobot_new_msg"));
        } else {
            notReadInfo.setVisibility(View.GONE);
        }
    }

    /**
     * ????????????????????????
     */
    private void connCustomerServiceSuccess(ZhiChiMessageBase base) {
        if (base == null || initModel == null) {
            return;
        }
        initModel.setAdminHelloWord(!TextUtils.isEmpty(base.getAdminHelloWord()) ? base.getAdminHelloWord() : initModel.getAdminHelloWord());
        initModel.setAdminTipTime(!TextUtils.isEmpty(base.getServiceOutTime()) ? base.getServiceOutTime() : initModel.getAdminTipTime());
        initModel.setAdminTipWord(!TextUtils.isEmpty(base.getServiceOutDoc()) ? base.getServiceOutDoc() : initModel.getAdminTipWord());

        //????????????
        zhiChiApi.connChannel(base.getWslinkBak(), base.getWslinkDefault(), initModel.getPartnerid(),
                base.getPuid(), info.getApp_key(), base.getWayHttp());
        createCustomerService(base.getAname(), base.getAface());
    }

    /**
     * ????????????????????????
     *
     * @param name ???????????????
     * @param face ???????????????
     */
    private void createCustomerService(String name, String face) {
        //????????????
        current_client_model = ZhiChiConstant.client_model_customService;
        if (SobotOption.sobotChatStatusListener != null) {
            //?????????????????????????????????
            SobotOption.sobotChatStatusListener.onChatStatusListener(SobotChatStatusMode.ZCServerConnectArtificial);
        }
        customerState = CustomerState.Online;
        isAboveZero = false;
        isComment = false;// ???????????? ????????? ?????????
        queueNum = 0;
        currentUserName = TextUtils.isEmpty(name) ? "" : name;
        //?????????xx????????????
        messageAdapter.addData(ChatUtils.getServiceAcceptTip(mAppContext, name));

        //?????????????????????????????????????????????????????????
        messageAdapter.removeKeyWordTranferItem();

        if (initModel.isAdminHelloWordFlag()) {
            if (!(initModel.isAdminHelloWordCountRule() && initModel.getUstatus() == ZhiChiConstant.ustatus_online)) {
                //?????????????????? ?????? ??????????????????????????????????????????????????? ???????????????????????????
                String adminHelloWord = ZCSobotApi.getCurrentInfoSetting(mAppContext) != null ? ZCSobotApi.getCurrentInfoSetting(mAppContext).getAdmin_hello_word() : "";
                //?????????????????????
                if (!TextUtils.isEmpty(adminHelloWord)) {
                    messageAdapter.addData(ChatUtils.getServiceHelloTip(name, face, adminHelloWord));
                } else {
                    messageAdapter.addData(ChatUtils.getServiceHelloTip(name, face, initModel.getAdminHelloWord()));
                }
            }
        }
        messageAdapter.notifyDataSetChanged();
        //????????????
        showLogicTitle(name, face, false);
        Message message = handler.obtainMessage();
        message.what = ZhiChiConstant.send_message_close;
        handler.sendMessage(message);
        showSwitchRobotBtn();
        //??????????????????
        createConsultingContent(0);
        //??????????????????
        createOrderCardContent(0);
        gotoLastItem();
        //??????????????????
        setBottomView(ZhiChiConstant.bottomViewtype_customer);
        mBottomViewtype = ZhiChiConstant.bottomViewtype_customer;

        // ??????????????????
        restartInputListener();
        stopUserInfoTimeTask();
        is_startCustomTimerTask = false;
        startUserInfoTimeTask(handler);
        hideItemTransferBtn();
        //????????????????????????
        et_sendmessage.setAutoCompleteEnable(false);
        //?????????????????????
        if (info.getAutoSendMsgMode().getAuto_send_msgtype() == ZCMessageTypeText) {
            //????????????????????????
            processAutoSendMsg(info);
        } else {
            //??????????????????????????????????????????????????????
            if (info.getAutoSendMsgMode() != null && info.getAutoSendMsgMode() != SobotAutoSendMsgMode.Default && current_client_model == ZhiChiConstant.client_model_customService && !TextUtils.isEmpty(info.getAutoSendMsgMode().getContent())) {
                if (info.getAutoSendMsgMode() == SobotAutoSendMsgMode.SendToOperator && customerState == CustomerState.Online) {
                    //????????????
                    String content = info.getAutoSendMsgMode().getContent();
                    if (info.getAutoSendMsgMode().getAuto_send_msgtype() == ZCMessageTypeFile) {
                        //????????????
                        File sendFile = new File(content);
                        if (sendFile.exists()) {
                            uploadFile(sendFile, handler, lv_message, messageAdapter, false);
                        }
                    } else if (info.getAutoSendMsgMode().getAuto_send_msgtype() == ZCMessageTypeVideo) {
                        //????????????
                        File sendFile = new File(content);
                        if (sendFile.exists()) {
                            uploadVideo(sendFile, null, messageAdapter);
                        }
                    } else if (info.getAutoSendMsgMode().getAuto_send_msgtype() == ZCMessageTypePhoto) {
                        //????????????
                        File sendFile = new File(content);
                        if (sendFile.exists()) {
                            uploadFile(sendFile, handler, lv_message, messageAdapter, false);
                        }
                    }
                }
            }
        }
        if (!isRemindTicketInfo) {
            processNewTicketMsg(handler);
        }
        if (!TextUtils.isEmpty(tempMsgContent)) {
            sendMsg(tempMsgContent);
            tempMsgContent = "";
        }
    }

    /**
     * ?????????????????????????????????
     */
    public void hideItemTransferBtn() {
        if (!isActive()) {
            return;
        }
        // ???????????????????????????????????????????????????
        lv_message.post(new Runnable() {

            @Override
            public void run() {

                for (int i = 0, count = lv_message.getChildCount(); i < count; i++) {
                    View child = lv_message.getChildAt(i);
                    if (child == null || child.getTag() == null || !(child.getTag() instanceof RichTextMessageHolder)) {
                        continue;
                    }
                    RichTextMessageHolder holder = (RichTextMessageHolder) child.getTag();
                    if (holder.message != null) {
                        holder.message.setShowTransferBtn(false);
                    }
                    holder.hideTransferBtn();
                }
            }
        });
    }

    /**
     * ??????????????????????????????
     */
    private void showCustomerOfflineTip() {
        if (initModel.isAdminNoneLineFlag()) {
            ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
            reply.setMsgType(null);
            String adminNoneLineTitle = ZCSobotApi.getCurrentInfoSetting(mAppContext) != null ? ZCSobotApi.getCurrentInfoSetting(mAppContext).getAdmin_offline_title() : "";
            if (!TextUtils.isEmpty(adminNoneLineTitle)) {
                reply.setMsg(adminNoneLineTitle);
            } else {
                if (TextUtils.isEmpty(initModel.getAdminNonelineTitle())) {
                    //??????????????????????????????????????????????????????????????????
                    return;
                }
                reply.setMsg(initModel.getAdminNonelineTitle());
            }
            reply.setRemindType(ZhiChiConstant.sobot_remind_type_customer_offline);
            ZhiChiMessageBase base = new ZhiChiMessageBase();
            base.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");
            base.setAnswer(reply);
            base.setAction(ZhiChiConstant.action_remind_info_post_msg);
            updateUiMessage(messageAdapter, base);
        }
    }

    /**
     * ????????????????????????
     */
    private void showCustomerUanbleTip() {
        ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
        reply.setMsgType(null);
        reply.setMsg(getResString("sobot_unable_transfer_to_customer_service"));
        reply.setRemindType(ZhiChiConstant.sobot_remind_type_unable_to_customer);
        ZhiChiMessageBase base = new ZhiChiMessageBase();
        base.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");
        base.setAnswer(reply);
        base.setAction(ZhiChiConstant.action_remind_info_post_msg);
        updateUiMessage(messageAdapter, base);
    }

    /**
     * ????????????????????? ?????????????????????????????????????????????
     */
    private void showCaiToCustomerTip() {
        ZhiChiMessageBase base = new ZhiChiMessageBase();
        base.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");
        base.setAction(ZhiChiConstant.action_remind_info_zhuanrengong);
        updateUiMessage(messageAdapter, base);
        gotoLastItem();
    }

    /**
     * ??????????????????????????????
     * ???????????????????????????
     *
     * @param num      ?????????????????????
     * @param status   ??????????????????????????????????????????7???????????????????????????????????????????????????????????????
     * @param queueDoc ??????????????????????????????
     */
    private void createCustomerQueue(String num, int status, String queueDoc, boolean isShowTips) {
        if (customerState == CustomerState.Queuing && !TextUtils.isEmpty(num)
                && Integer.parseInt(num) > 0) {
            stopUserInfoTimeTask();
            stopCustomTimeTask();
            stopInputListener();

            queueNum = Integer.parseInt(num);
            //???????????????????????????
            if (status != ZhiChiConstant.transfer_robot_custom_max_status && isShowTips) {
                showInLineHint(queueDoc);
            }

            if (type == ZhiChiConstant.type_custom_only) {
                showLogicTitle(getResString("sobot_in_line"), null, false);
                setBottomView(ZhiChiConstant.bottomViewtype_onlycustomer_paidui);
                mBottomViewtype = ZhiChiConstant.bottomViewtype_onlycustomer_paidui;
            } else {
                showLogicTitle(initModel.getRobotName(), initModel.getRobotLogo(), false);
                setBottomView(ZhiChiConstant.bottomViewtype_paidui);
                mBottomViewtype = ZhiChiConstant.bottomViewtype_paidui;
            }

            queueTimes = queueTimes + 1;
            if (type == ZhiChiConstant.type_custom_first) {
                if (queueTimes == 1) {
                    //?????????????????????????????????????????????????????????
                    remindRobotMessage(handler, initModel, info);
                }
            }
            showSwitchRobotBtn();
        }
    }

    /**
     * ???????????????cid?????????
     */
    private void queryCids() {
        //??????initmodel ??????  querycid?????????????????????????????????????????????????????????????????????
        if (initModel == null || queryCidsStatus == ZhiChiConstant.QUERY_CIDS_STATUS_LOADING
                || queryCidsStatus == ZhiChiConstant.QUERY_CIDS_STATUS_SUCCESS) {
            return;
        }
        long time = SharedPreferencesUtil.getLongData(mAppContext, ZhiChiConstant.SOBOT_SCOPE_TIME, 0);
        queryCidsStatus = ZhiChiConstant.QUERY_CIDS_STATUS_LOADING;
        // ???????????????cid?????????
        zhiChiApi.queryCids(SobotChatFSFragment.this, initModel.getPartnerid(), time, new StringResultCallBack<ZhiChiCidsModel>() {

            @Override
            public void onSuccess(ZhiChiCidsModel data) {
                if (!isActive()) {
                    return;
                }
                queryCidsStatus = ZhiChiConstant.QUERY_CIDS_STATUS_SUCCESS;
                cids = data.getCids();
                if (cids != null) {
                    boolean hasRepeat = false;
                    for (int i = 0; i < cids.size(); i++) {
                        if (cids.get(i).equals(initModel.getCid())) {
                            hasRepeat = true;
                            break;
                        }
                    }
                    if (!hasRepeat) {
                        cids.add(initModel.getCid());
                    }
                    Collections.reverse(cids);
                }
                //??????????????????
                getHistoryMessage(true);
            }

            @Override
            public void onFailure(Exception e, String des) {
                queryCidsStatus = ZhiChiConstant.QUERY_CIDS_STATUS_FAILURE;
            }
        });
    }

    private void showInitError() {
        showLogicTitle("", null, false);
        loading_anim_view.setVisibility(View.GONE);
        txt_loading.setVisibility(View.GONE);
        textReConnect.setVisibility(View.VISIBLE);
        icon_nonet.setVisibility(View.VISIBLE);
        btn_reconnect.setVisibility(View.VISIBLE);
        et_sendmessage.setVisibility(View.GONE);
        relative.setVisibility(View.GONE);
        welcome.setVisibility(View.VISIBLE);
    }

    /*
     * ??????????????????
     *
     */
    @Override
    public void sendConsultingContent() {
        sendCardMsg(info.getConsultingContent());
    }

    /**
     * @param base
     * @param type
     * @param questionFlag 0 ????????????????????????
     *                     1 ??????docId?????????
     *                     2 ???????????????
     * @param docId        ????????????Null
     */
    @Override
    public void sendMessageToRobot(ZhiChiMessageBase base, int type, int questionFlag, String docId) {
        sendMessageToRobot(base, type, questionFlag, docId, null);
    }

    /*??????0?????????????????? 1?????????  2?????????  3????????? 4??????????????? 5???????????????*/
    @Override
    public void sendMessageToRobot(ZhiChiMessageBase base, int type, int questionFlag, String docId, String multiRoundMsg) {
        if (type == 5) {
            sendLocation(base.getId(), base.getAnswer().getLocationData(), handler, false);
        }
        if (type == 4) {
            sendMsgToRobot(base, SEND_TEXT, questionFlag, docId, multiRoundMsg);
        }

        /*????????????*/
        else if (type == 3) {
            // ???????????????url ???????????? ???????????????????????????
            messageAdapter.updatePicStatusById(base.getId(), base.getSendSuccessState());
            messageAdapter.notifyDataSetChanged();
            ChatUtils.sendPicture(mAppContext, initModel.getCid(), initModel.getPartnerid(),
                    base.getContent(), handler, base.getId(), lv_message, messageAdapter);
        }

        /*????????????*/
        else if (type == 2) {
            // ?????????????????????
            sendVoiceMessageToHandler(base.getId(), base.getContent(), base.getAnswer()
                    .getDuration(), ZhiChiConstant.MSG_SEND_STATUS_LOADING, UPDATE_VOICE, handler);
            sendVoice(base.getId(), base.getAnswer().getDuration(), initModel.getCid(),
                    initModel.getPartnerid(), base.getContent(), handler);
        }

        /*????????????*/
        else if (type == 1) {
            // ???????????????
            sendMsgToRobot(base, UPDATE_TEXT, questionFlag, docId);
        }

        /*???????????????*/
        else if (type == 0) {

            if (!isSessionOver) {
                // ???????????????
                ZhiChiReplyAnswer answer = new ZhiChiReplyAnswer();
                answer.setMsgType(ZhiChiConstant.message_type_text + "");
                answer.setMsg(base.getContent());
                base.setAnswer(answer);
                base.setSenderType(ZhiChiConstant.message_sender_type_customer + "");
                if (base.getId() == null || TextUtils.isEmpty(base.getId())) {
                    String msgId = System.currentTimeMillis() + "";
                    base.setId(msgId);
                    updateUiMessage(messageAdapter, base);
                }
                sendMessageWithLogic(base.getId(), base.getContent(), initModel, handler, current_client_model, questionFlag, docId);
            } else {
                showOutlineTip(initModel, 1);
            }
        }
        gotoLastItem();
    }

    /**
     * ????????????????????????
     */
    @Override
    public void doClickTransferBtn(ZhiChiMessageBase base) {
        //???????????????
        hidePanelAndKeyboard(mPanelRoot);
        doEmoticonBtn2Blur();
        if(base!=null) {
            int temptransferType = base.getTransferType();
            if(temptransferType==0) {
                if (Integer.parseInt(base.getAnswerType()) == 1) {
                    temptransferType = 6;
                }else if(Integer.parseInt(base.getAnswerType()) == 2){
                    temptransferType = 7;
                }else if(Integer.parseInt(base.getAnswerType()) == 3){
                    temptransferType = 9;
                }else if(Integer.parseInt(base.getAnswerType()) == 4){
                    temptransferType = 8;
                }
            }
            transfer2Custom(null, null, null, true, temptransferType, base.getDocId(), base.getOriginQuestion(), "1");
        }else {
            transfer2Custom(null, null, null, true, "1");
        }
    }
    public void doClickTransferBtn() {
        //???????????????
        hidePanelAndKeyboard(mPanelRoot);
        doEmoticonBtn2Blur();
        transfer2Custom(null, null, null, true, "1");
    }

    // ???????????????????????????
    @Override
    public void clickAudioItem(ZhiChiMessageBase message) {
        if (mAudioPlayPresenter == null) {
            mAudioPlayPresenter = new AudioPlayPresenter(mAppContext);
        }
        if (mAudioPlayCallBack == null) {
            mAudioPlayCallBack = new AudioPlayCallBack() {
                @Override
                public void onPlayStart(ZhiChiMessageBase mCurrentMsg) {
                    showVoiceAnim(mCurrentMsg, true);
                }

                @Override
                public void onPlayEnd(ZhiChiMessageBase mCurrentMsg) {
                    showVoiceAnim(mCurrentMsg, false);
                }
            };
        }
        mAudioPlayPresenter.clickAudio(message, mAudioPlayCallBack);
    }

    @Override
    public void sendMessage(String content) {
        sendMsg(content);
    }

    @Override
    public void removeMessageByMsgId(String msgid) {
        if (messageAdapter != null && !TextUtils.isEmpty(msgid)) {
            messageAdapter.removeByMsgId(msgid);
            messageAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void addMessage(ZhiChiMessageBase message) {
        if (message != null) {
            messageAdapter.justAddData(message);
            messageAdapter.notifyDataSetChanged();
        }
    }

    public void showVoiceAnim(final ZhiChiMessageBase info, final boolean isShow) {
        if (!isActive()) {
            return;
        }
        lv_message.post(new Runnable() {

            @Override
            public void run() {
                if (info == null) {
                    return;
                }
                for (int i = 0, count = lv_message.getChildCount(); i < count; i++) {
                    View child = lv_message.getChildAt(i);
                    if (child == null || child.getTag() == null || !(child.getTag() instanceof VoiceMessageHolder)) {
                        continue;
                    }
                    VoiceMessageHolder holder = (VoiceMessageHolder) child.getTag();
                    holder.stopAnim();
                    if (holder.message == info) {
                        if (isShow) {
                            holder.startAnim();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void hidePanelAndKeyboard() {
        hidePanelAndKeyboard(mPanelRoot);
    }

    /**
     * ??????????????????
     *
     * @param revaluateFlag true ???  false ???
     * @param message       ???????????? model
     */
    @Override
    public void doRevaluate(final boolean revaluateFlag, final ZhiChiMessageBase message) {
        if (isSessionOver) {
            showOutlineTip(initModel, 1);
            CustomToast.makeText(mAppContext, getResString("sobot_ding_cai_sessionoff"), 1500).show();
            return;
        }
        CustomToast.makeText(mAppContext, revaluateFlag ? getResString("sobot_ding_cai_like") : getResString("sobot_ding_cai_dislike"), 1500).show();
        zhiChiApi.rbAnswerComment(SobotChatFSFragment.this, message.getMsgId(), initModel.getPartnerid(), initModel.getCid(), initModel.getRobotid(),
                message.getDocId(), message.getDocName(), revaluateFlag, message.getOriginQuestion(), message.getAnswerType(), new StringResultCallBack<CommonModelBase>() {
                    @Override
                    public void onSuccess(CommonModelBase data) {
                        if (!isActive()) {
                            return;
                        }
                        //if (ZhiChiConstant.client_sendmsg_to_custom_fali.equals(data.getStatus())) {
                        //  customerServiceOffline(initModel, 1);
                        //} else if (ZhiChiConstant.client_sendmsg_to_custom_success.equals(data.getStatus())) {
                        //???????????????????????????
                        message.setRevaluateState(revaluateFlag ? 2 : 3);
                        refreshItemByCategory(RichTextMessageHolder.class);
                        if ((!TextUtils.isEmpty(message.getAnswerType()) && message.getAnswerType().startsWith("152"))) {
                            refreshItemByCategory(RobotTemplateMessageHolder1.class);
                            refreshItemByCategory(RobotTemplateMessageHolder2.class);
                            refreshItemByCategory(RobotTemplateMessageHolder3.class);
                            refreshItemByCategory(RobotTemplateMessageHolder4.class);
                            refreshItemByCategory(RobotTemplateMessageHolder5.class);
                            refreshItemByCategory(RobotTemplateMessageHolder6.class);
                        }
                        //?????????????????????
                        if (initModel.getRealuateTransferFlag() == 1 && current_client_model != ZhiChiConstant.client_model_customService && !revaluateFlag && type != ZhiChiConstant.type_robot_only) {
                            //??????  ???????????????????????? ????????????????????????????????????
                            zhiChiApi.insertSysMsg(SobotChatFSFragment.this, initModel.getCid(), initModel.getPartnerid(), ResourceUtils.getResString(getSobotActivity(), "sobot_cant_solve_problem") + ResourceUtils.getResString(getSobotActivity(), "sobot_customer_service"), "?????????????????????", new StringResultCallBack<BaseCode>() {
                                @Override
                                public void onSuccess(BaseCode baseCode) {
                                    showCaiToCustomerTip();
                                }

                                @Override
                                public void onFailure(Exception e, String des) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Exception e, String des) {
                        ToastUtil.showToast(mAppContext, ResourceUtils.getResString(getContext(), "sobot_net_work_err"));
                    }
                });
    }

    /**
     * ??????????????????
     *
     * @param evaluateFlag true ????????????  false ??????????????????
     * @param message      data
     */
    @Override
    public void doEvaluate(final boolean evaluateFlag, final ZhiChiMessageBase message) {
        if (initModel == null || message == null) {
            return;
        }
        SobotEvaluateModel sobotEvaluateModel = message.getSobotEvaluateModel();
        if (sobotEvaluateModel == null) {
            return;
        }
        if (evaluateFlag) {
            SobotCommentParam sobotCommentParam = new SobotCommentParam();
            sobotCommentParam.setType("1");
            sobotCommentParam.setScore(message.getSobotEvaluateModel().getScore() + "");
            sobotCommentParam.setScoreFlag(message.getSobotEvaluateModel().getScoreFlag());
            sobotCommentParam.setCommentType(0);
            sobotCommentParam.setProblem(sobotEvaluateModel.getProblem());
            sobotCommentParam.setIsresolve(sobotEvaluateModel.getIsResolved());
            zhiChiApi.comment(SobotChatFSFragment.this, initModel.getCid(), initModel.getPartnerid(), sobotCommentParam, new StringResultCallBack<CommonModel>() {
                @Override
                public void onSuccess(CommonModel commonModel) {
                    if (!isActive()) {
                        return;
                    }
                    Intent intent = new Intent();
                    intent.setAction(ZhiChiConstants.dcrc_comment_state);
                    intent.putExtra("commentState", true);
                    intent.putExtra("commentType", 0);
                    intent.putExtra("score", message.getSobotEvaluateModel().getScore());
                    intent.putExtra("isResolved", message.getSobotEvaluateModel().getIsResolved());
                    CommonUtils.sendLocalBroadcast(mAppContext, intent);
                }

                @Override
                public void onFailure(Exception e, String des) {

                }
            });
        } else {
            submitEvaluation(false, sobotEvaluateModel.getScore(), sobotEvaluateModel.getIsResolved(), sobotEvaluateModel.getProblem());
        }

    }

    /**
     * ????????????????????????viewHolder
     *
     * @param clz viewHolder.class
     */
    private <T> void refreshItemByCategory(final Class<T> clz) {
        if (!isActive()) {
            return;
        }
        lv_message.post(new Runnable() {

            @Override
            public void run() {
                for (int i = 0, count = lv_message.getChildCount(); i < count; i++) {
                    View child = lv_message.getChildAt(i);
                    if (child == null || child.getTag() == null) {
                        continue;
                    }
                    if (clz == RichTextMessageHolder.class && child.getTag() instanceof RichTextMessageHolder) {
                        RichTextMessageHolder holder = (RichTextMessageHolder) child.getTag();
                        holder.refreshItem();
                    } else if (clz == CusEvaluateMessageHolder.class && child.getTag() instanceof CusEvaluateMessageHolder) {
                        CusEvaluateMessageHolder holder = (CusEvaluateMessageHolder) child.getTag();
                        holder.refreshItem();
                    } else if (clz == RobotTemplateMessageHolder1.class && child.getTag() instanceof RobotTemplateMessageHolder1) {
                        RobotTemplateMessageHolder1 holder = (RobotTemplateMessageHolder1) child.getTag();
                        holder.refreshRevaluateItem();
                    } else if (clz == RobotTemplateMessageHolder2.class && child.getTag() instanceof RobotTemplateMessageHolder2) {
                        RobotTemplateMessageHolder2 holder = (RobotTemplateMessageHolder2) child.getTag();
                        holder.refreshRevaluateItem();
                    } else if (clz == RobotTemplateMessageHolder3.class && child.getTag() instanceof RobotTemplateMessageHolder3) {
                        RobotTemplateMessageHolder3 holder = (RobotTemplateMessageHolder3) child.getTag();
                        holder.refreshRevaluateItem();
                    } else if (clz == RobotTemplateMessageHolder4.class && child.getTag() instanceof RobotTemplateMessageHolder4) {
                        RobotTemplateMessageHolder4 holder = (RobotTemplateMessageHolder4) child.getTag();
                        holder.refreshRevaluateItem();
                    } else if (clz == RobotTemplateMessageHolder5.class && child.getTag() instanceof RobotTemplateMessageHolder5) {
                        RobotTemplateMessageHolder5 holder = (RobotTemplateMessageHolder5) child.getTag();
                        holder.refreshRevaluateItem();
                    } else if (clz == RobotTemplateMessageHolder6.class && child.getTag() instanceof RobotTemplateMessageHolder6) {
                        RobotTemplateMessageHolder6 holder = (RobotTemplateMessageHolder6) child.getTag();
                        holder.refreshRevaluateItem();
                    }
                }
            }
        });
    }

    //??????????????????
    private void getAnnouncement() {
        if (!TextUtils.isEmpty(initModel.getAnnounceClickUrl()) && initModel.getAnnounceClickFlag()) {
//            sobot_announcement_right_icon.setVisibility(View.VISIBLE);
            sobot_announcement_right_icon.setVisibility(View.GONE);
            sobot_announcement_title.setTextColor(ContextCompat.getColor(getContext(), ResourceUtils.getResColorId(getContext(), "sobot_announcement_title_color_2")));
        } else {
            sobot_announcement_title.setTextColor(ContextCompat.getColor(getContext(), ResourceUtils.getResColorId(getContext(), "sobot_announcement_title_color")));
            sobot_announcement_right_icon.setVisibility(View.GONE);
        }

        if (initModel.getAnnounceMsgFlag() && initModel.isAnnounceTopFlag() && !TextUtils.isEmpty(initModel.getAnnounceMsg())) {
            sobot_announcement.setVisibility(View.VISIBLE);
            sobot_announcement_title.setText(initModel.getAnnounceMsg());
            sobot_announcement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // ???????????????
                    if (!TextUtils.isEmpty(initModel.getAnnounceClickUrl()) && initModel.getAnnounceClickFlag()) {
                        if (SobotOption.hyperlinkListener != null) {
                            SobotOption.hyperlinkListener.onUrlClick(initModel.getAnnounceClickUrl());
                            return;
                        }
                        if (SobotOption.newHyperlinkListener != null) {
                            //????????????true,??????;false ?????????
                            boolean isIntercept = SobotOption.newHyperlinkListener.onUrlClick(getSobotActivity(), initModel.getAnnounceClickUrl());
                            if (isIntercept) {
                                return;
                            }
                        }
                        Intent intent = new Intent(mAppContext, WebViewActivity.class);
                        intent.putExtra("url", initModel.getAnnounceClickUrl());
                        startActivity(intent);
                    }
                }
            });
        } else {
            sobot_announcement.setVisibility(View.GONE);
        }
    }

    /**
     * ??????????????????UI
     *
     * @param viewType
     */
    public void setBottomView(int viewType) {
        welcome.setVisibility(View.GONE);
        relative.setVisibility(View.VISIBLE);
        chat_main.setVisibility(View.VISIBLE);
        et_sendmessage.setVisibility(View.VISIBLE);
        sobot_ll_restart_talk.setVisibility(View.GONE);
        sobot_ll_bottom.setVisibility(View.VISIBLE);

        hideReLoading();
        if (isUserBlack()) {
            sobot_ll_restart_talk.setVisibility(View.GONE);
            sobot_ll_bottom.setVisibility(View.VISIBLE);
            btn_model_voice.setVisibility(View.GONE);
            btn_emoticon_view.setVisibility(View.GONE);
        }
        if (info.isHideMenuSatisfaction()) {
            sobot_tv_satisfaction.setVisibility(View.GONE);
        } else {
            sobot_tv_satisfaction.setVisibility(View.VISIBLE);
        }
        sobot_txt_restart_talk.setVisibility(View.VISIBLE);
        sobot_tv_message.setVisibility(View.VISIBLE);

        LogUtils.i("setBottomView:" + viewType);
        switch (viewType) {
            case ZhiChiConstant.bottomViewtype_onlyrobot:
                // ????????????
                showVoiceBtn();
                if (image_reLoading.getVisibility() == View.VISIBLE) {
                    sobot_ll_bottom.setVisibility(View.VISIBLE);/* ?????????????????? */
                    edittext_layout.setVisibility(View.VISIBLE);/* ????????????????????? */
                    sobot_ll_restart_talk.setVisibility(View.GONE);

                    if (btn_press_to_speak.getVisibility() == View.VISIBLE) {
                        btn_press_to_speak.setVisibility(View.GONE);
                    }
                    btn_set_mode_rengong.setClickable(false);
                    btn_set_mode_rengong.setVisibility(View.GONE);
                }
                btn_emoticon_view.setVisibility(View.GONE);
                btn_upload_view.setVisibility(View.VISIBLE);
                btn_send.setVisibility(View.GONE);
                break;
            case ZhiChiConstant.bottomViewtype_robot:
                //??????????????????
                if (info.isArtificialIntelligence() && type == ZhiChiConstant.type_robot_first) {
                    //??????????????????????????????????????????
                    if (showTimeVisiableCustomBtn >= info.getArtificialIntelligenceNum()) {
                        btn_set_mode_rengong.setVisibility(View.VISIBLE);
                    } else {
                        btn_set_mode_rengong.setVisibility(View.GONE);
                    }
                } else {
                    btn_set_mode_rengong.setVisibility(View.VISIBLE);
                }

                btn_set_mode_rengong.setClickable(true);
                showVoiceBtn();
                if (Build.VERSION.SDK_INT >= 11)
                    btn_set_mode_rengong.setAlpha(1f);
                if (image_reLoading.getVisibility() == View.VISIBLE) {
                    sobot_ll_bottom.setVisibility(View.VISIBLE);/* ?????????????????? */
                    edittext_layout.setVisibility(View.VISIBLE);/* ????????????????????? */
                    sobot_ll_restart_talk.setVisibility(View.GONE);

                    if (btn_press_to_speak.getVisibility() == View.VISIBLE) {
                        btn_press_to_speak.setVisibility(View.GONE);
                    }
                    btn_set_mode_rengong.setClickable(true);
                    btn_set_mode_rengong.setEnabled(true);
                }
                btn_upload_view.setVisibility(View.VISIBLE);
                btn_emoticon_view.setVisibility(View.GONE);
                btn_send.setVisibility(View.GONE);
                break;
            case ZhiChiConstant.bottomViewtype_customer:
                //???????????????
                hideRobotVoiceHint();
                btn_model_edit.setVisibility(View.GONE);
                btn_set_mode_rengong.setVisibility(View.GONE);
                btn_upload_view.setVisibility(View.VISIBLE);
                btn_send.setVisibility(View.GONE);
                showEmotionBtn();
                showVoiceBtn();
                btn_model_voice.setEnabled(true);
                btn_model_voice.setClickable(true);
                btn_upload_view.setEnabled(true);
                btn_upload_view.setClickable(true);
                btn_emoticon_view.setClickable(true);
                btn_emoticon_view.setEnabled(true);
                if (Build.VERSION.SDK_INT >= 11) {
                    btn_model_voice.setAlpha(1f);
                    btn_upload_view.setAlpha(1f);
                }

                edittext_layout.setVisibility(View.VISIBLE);
                sobot_ll_bottom.setVisibility(View.VISIBLE);
                btn_press_to_speak.setVisibility(View.GONE);
                btn_press_to_speak.setClickable(true);
                btn_press_to_speak.setEnabled(true);
                txt_speak_content.setText(getResString("sobot_press_say"));
                break;
            case ZhiChiConstant.bottomViewtype_onlycustomer_paidui:
                //??????????????????
                onlyCustomPaidui();

                hidePanelAndKeyboard(mPanelRoot);
                if (lv_message.getLastVisiblePosition() != messageAdapter.getCount()) {
                    lv_message.setSelection(messageAdapter.getCount());
                }
                break;
            case ZhiChiConstant.bottomViewtype_outline:
                //?????????
                hideReLoading();
                hidePanelAndKeyboard(mPanelRoot);/*????????????*/
                sobot_ll_bottom.setVisibility(View.GONE);
                sobot_ll_restart_talk.setVisibility(View.VISIBLE);
                if (info.isHideMenuSatisfaction()) {
                    sobot_tv_satisfaction.setVisibility(View.GONE);
                } else {
                    sobot_tv_satisfaction.setVisibility(View.VISIBLE);
                }
                sobot_txt_restart_talk.setVisibility(View.VISIBLE);
                btn_model_edit.setVisibility(View.GONE);
                if (info.isHideMenuLeave()) {
                    sobot_tv_message.setVisibility(View.GONE);
                } else {
                    sobot_tv_message.setVisibility(initModel.getMsgFlag() == ZhiChiConstant.sobot_msg_flag_close ? View
                            .GONE : View.VISIBLE);
                }
                btn_model_voice.setVisibility(View.GONE);
                lv_message.setSelection(messageAdapter.getCount());
                break;
            case ZhiChiConstant.bottomViewtype_paidui:
                //????????????????????????
                if (btn_press_to_speak.getVisibility() == View.GONE) {
                    showVoiceBtn();
                }
                btn_set_mode_rengong.setVisibility(View.VISIBLE);
                btn_emoticon_view.setVisibility(View.GONE);
                if (image_reLoading.getVisibility() == View.VISIBLE) {
                    sobot_ll_bottom.setVisibility(View.VISIBLE);/* ?????????????????? */
                    edittext_layout.setVisibility(View.VISIBLE);/* ????????????????????? */
                    btn_model_voice.setVisibility(View.GONE);
                    sobot_ll_restart_talk.setVisibility(View.GONE);

                    if (btn_press_to_speak.getVisibility() == View.VISIBLE) {
                        btn_press_to_speak.setVisibility(View.GONE);
                    }
                }
                break;
            case ZhiChiConstant.bottomViewtype_custom_only_msgclose:
                sobot_ll_restart_talk.setVisibility(View.VISIBLE);

                sobot_ll_bottom.setVisibility(View.GONE);
                if (image_reLoading.getVisibility() == View.VISIBLE) {
                    sobot_txt_restart_talk.setVisibility(View.VISIBLE);
                    sobot_txt_restart_talk.setClickable(true);
                    sobot_txt_restart_talk.setEnabled(true);
                }
                if (initModel.getMsgFlag() == ZhiChiConstant.sobot_msg_flag_close) {
                    //????????????
                    sobot_tv_satisfaction.setVisibility(View.INVISIBLE);
                    sobot_tv_message.setVisibility(View.INVISIBLE);
                } else {
                    sobot_tv_satisfaction.setVisibility(View.GONE);
                    sobot_tv_message.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    //??????????????????UI??????
    private void onlyCustomPaidui() {
        if (SobotOption.sobotChatStatusListener != null) {
            //?????????????????????
            SobotOption.sobotChatStatusListener.onChatStatusListener(SobotChatStatusMode.ZCServerConnectWaiting);
        }
        sobot_ll_bottom.setVisibility(View.VISIBLE);

        btn_set_mode_rengong.setVisibility(View.GONE);
        btn_set_mode_rengong.setClickable(false);

        btn_upload_view.setVisibility(View.VISIBLE);
        btn_send.setVisibility(View.GONE);
        btn_upload_view.setClickable(false);
        btn_upload_view.setEnabled(false);

        showEmotionBtn();
        btn_emoticon_view.setClickable(false);
        btn_emoticon_view.setEnabled(false);

        showVoiceBtn();
        btn_model_voice.setClickable(false);
        btn_model_voice.setEnabled(false);
        btn_model_voice.setVisibility(View.GONE);


        edittext_layout.setVisibility(View.GONE);
        btn_press_to_speak.setClickable(false);
        btn_press_to_speak.setEnabled(false);
        btn_press_to_speak.setVisibility(View.VISIBLE);
        txt_speak_content.setText(getResString("sobot_in_line"));
        showLogicTitle(getResString("sobot_in_line"), null, false);
        if (sobot_ll_restart_talk.getVisibility() == View.VISIBLE) {
            sobot_ll_restart_talk.setVisibility(View.GONE);
        }
    }

    //3.0.3 type 0 ?????????????????????????????????1 ????????????????????????????????????????????????????????????????????????
    private void createConsultingContent(int type) {
        ConsultingContent consultingContent = info.getConsultingContent();
        if (consultingContent != null && !TextUtils.isEmpty(consultingContent.getSobotGoodsTitle()) && !TextUtils.isEmpty(consultingContent.getSobotGoodsFromUrl())) {
            ZhiChiMessageBase zhichiMessageBase = new ZhiChiMessageBase();
            zhichiMessageBase.setSenderType(ZhiChiConstant.message_sender_type_consult_info + "");
            if (!TextUtils.isEmpty(consultingContent.getSobotGoodsImgUrl())) {
                zhichiMessageBase.setPicurl(consultingContent.getSobotGoodsImgUrl());
            }
            ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
            zhichiMessageBase.setAnswer(reply);
            zhichiMessageBase.setContent(consultingContent.getSobotGoodsTitle());
            zhichiMessageBase.setUrl(consultingContent.getSobotGoodsFromUrl());
            zhichiMessageBase.setCid(initModel == null ? "" : initModel.getCid());
            zhichiMessageBase.setAname(consultingContent.getSobotGoodsLable());
            zhichiMessageBase.setReceiverFace(consultingContent.getSobotGoodsDescribe());

            zhichiMessageBase.setAction(ZhiChiConstant.action_consultingContent_info);
            updateUiMessage(messageAdapter, zhichiMessageBase);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    lv_message.setSelection(messageAdapter.getCount());
                }
            });
            if (consultingContent.isAutoSend()) {
                if (type == 1) {
                    //???????????????????????????????????????????????????????????????????????????
                    if (consultingContent.isEveryTimeAutoSend()) {
                        sendConsultingContent();
                    }
                } else {
                    sendConsultingContent();
                }
            }
        } else {
            if (messageAdapter != null) {
                messageAdapter.removeConsulting();
            }
        }
    }

    //????????????????????????????????????isAutoSend???????????????????????????
    //3.0.3 type 0 ?????????????????????????????????1 ????????????????????????????????????????????????????????????????????????
    private void createOrderCardContent(int type) {
        OrderCardContentModel orderCardContent = info.getOrderGoodsInfo();
        if (orderCardContent != null && !TextUtils.isEmpty(orderCardContent.getOrderCode()) && orderCardContent.isAutoSend()) {
            if (type == 1) {
                //???????????????????????????????????????????????????????????????????????????
                if (orderCardContent.isEveryTimeAutoSend()) {
                    sendOrderCardMsg(orderCardContent);
                }
            } else {
                sendOrderCardMsg(orderCardContent);
            }
        }
    }


    /**
     * ?????????????????????????????????
     */
    protected void onCloseMenuClick() {
        hidePanelAndKeyboard(mPanelRoot);
        if (isActive()) {
            if (info.isShowCloseSatisfaction()) {
                if (isAboveZero && !isComment) {
                    // ????????? ??????????????????????????? ?????? ????????????
                    mEvaluateDialog = ChatUtils.showEvaluateDialog(getSobotActivity(), isSessionOver, true, true, initModel,
                            current_client_model, 1, currentUserName, 5, 0, "", false, true);
                    return;
                } else {
                    customerServiceOffline(initModel, 1);
                    ChatUtils.userLogout(mAppContext);
                }
            } else {
                customerServiceOffline(initModel, 1);
                ChatUtils.userLogout(mAppContext);
            }
            finish();
        }
    }

    /**
     * ???????????????????????????  ???????????????????????????  ???????????? ??????
     */
    protected void onLeftBackColseClick() {
        hidePanelAndKeyboard(mPanelRoot);
        if (isActive()) {
            if (info.isShowSatisfaction()) {
                if (isAboveZero && !isComment) {
                    // ????????? ??????????????????????????? ?????? ????????????
                    mEvaluateDialog = ChatUtils.showEvaluateDialog(getSobotActivity(), isSessionOver, true, true, initModel,
                            current_client_model, 1, currentUserName, 5, 0, "", false, true);
                    return;
                } else {
                    customerServiceOffline(initModel, 1);
                    ChatUtils.userLogout(mAppContext);
                }
            } else {
                customerServiceOffline(initModel, 1);
                ChatUtils.userLogout(mAppContext);
            }
            finish();
        }
    }

    /**
     * ???????????????????????????????????????  ??????????????????????????????????????????
     */
    private void resetBtnUploadAndSend() {
        if (et_sendmessage.getText().toString().length() > 0) {
            btn_upload_view.setVisibility(View.GONE);
            btn_send.setVisibility(View.VISIBLE);
        } else {
            btn_send.setVisibility(View.GONE);
            btn_upload_view.setVisibility(View.VISIBLE);
            btn_upload_view.setEnabled(true);
            btn_upload_view.setClickable(true);
            if (Build.VERSION.SDK_INT >= 11) {
                btn_upload_view.setAlpha(1f);
            }
        }
    }

    /**
     * ?????????????????????????????????title
     * ?????????????????????title???????????????????????????????????????
     *
     * @param title       ???????????????????????????????????????????????? ??????????????????
     * @param avatarUrl   ??????
     * @param ignoreLogic ??????????????????????????????
     */
    private void showLogicTitle(String title, String avatarUrl, boolean ignoreLogic) {
        if (initModel != null) {
            String avatarStr = ChatUtils.getLogicAvatar(mAppContext, ignoreLogic, avatarUrl, initModel.getCompanyLogo());
            //??????????????????,true ??????;false ??????,??????true
            boolean isShowAvatar = SharedPreferencesUtil.getBooleanData(getContext(), ZhiChiConstant.SOBOT_CHAT_AVATAR_IS_SHOW, true);
            if (TextUtils.isEmpty(avatarUrl)) {
                //?????????????????????????????????
                isShowAvatar = false;
            }
            setAvatar(avatarStr, isShowAvatar);
            String str = ChatUtils.getLogicTitle(mAppContext, ignoreLogic, title, initModel.getCompanyName());
            //??????????????????,true ??????;false ??????,??????false
            boolean isShowTitle = SharedPreferencesUtil.getBooleanData(getContext(), ZhiChiConstant.SOBOT_CHAT_TITLE_IS_SHOW, false);
            if (TextUtils.isEmpty(avatarUrl)) {
                //?????????????????????????????????
                isShowTitle = true;
            }
            setTitle(str, isShowTitle);
        }
    }

    // ??????????????????????????????
    public void setTitle(CharSequence title, boolean isShowTitle) {
        if (isShowTitle) {
            mTitleTextView.setVisibility(View.VISIBLE);
        } else {
            mTitleTextView.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(title)) {
            mTitleTextView.setText(title);
        }
        applyTitleTextColor(mTitleTextView);
    }

    // ??????????????????
    public void setAvatar(String avatarUrl, boolean isShowAvatar) {
        if (isShowAvatar) {
            mAvatarIV.setVisibility(View.VISIBLE);
            mAvatarIV.setRoundAsCircle(true);
            mAvatarIV.setStrokeWidth(ScreenUtils.dip2px(getContext(), 0.4f));
            mAvatarIV.setStrokeColor(ResourceUtils.getResColorValue(getContext(), "sobot_line_1dp"));
            if (!TextUtils.isEmpty(avatarUrl)) {
                SobotBitmapUtil.display(getContext(), avatarUrl, mAvatarIV);
            } else {
                SobotBitmapUtil.display(getContext(), getResDrawableId("sobot_robot"), mAvatarIV);
            }
        } else {
            mAvatarIV.setVisibility(View.GONE);
        }
    }


    // ????????????????????????
    public void setAvatar(int avatarUrl, boolean isShowAvatar) {
        if (isShowAvatar) {
            mAvatarIV.setVisibility(View.VISIBLE);
            mAvatarIV.setRoundAsCircle(true);
            mAvatarIV.setStrokeWidth(ScreenUtils.dip2px(getContext(), 0.4f));
            mAvatarIV.setStrokeColor(ResourceUtils.getResColorValue(getContext(), "sobot_line_1dp"));
            SobotBitmapUtil.display(getContext(), avatarUrl, mAvatarIV);
        } else {
            mAvatarIV.setVisibility(View.GONE);
        }
    }

    private SobotBackDialog sobotBackDialog;//???????????????????????????

    /**
     * ???????????????????????????
     */
    protected void onLeftMenuClick() {
        //?????????????????????????????????????????????????????????????????????????????????0????????????????????????????????????????????????
        showTimeVisiableCustomBtn = 0;
        hidePanelAndKeyboard(mPanelRoot);
        if (!isSessionOver && info.isShowLeftBackPop()) {//?????????????????????????????????
            sobotBackDialog = new SobotBackDialog(getSobotActivity(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sobotBackDialog.dismiss();
                    if (v.getId() == ResourceUtils.getResId(getContext(), "sobot_btn_cancle_conversation")) {
                        //????????????
                        onLeftBackColseClick();
                    } else if (v.getId() == ResourceUtils.getResId(getContext(), "sobot_btn_temporary_leave")) {
                        //????????????
                        if (isActive()) {
                            //???????????????????????? ?????????????????????????????????  ???????????????????????????????????????????????????
                            if (mPanelRoot.getVisibility() == View.VISIBLE) {
                                hidePanelAndKeyboard(mPanelRoot);
                                return;
                            }
                            finish();
                        }
                    }
                }
            });
            sobotBackDialog.show();
        } else {
            onBackPress();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (SobotOption.functionClickListener != null) {
            SobotOption.functionClickListener.onClickFunction(getSobotActivity(), SobotFunctionType.ZC_CloseChat);
        }
    }

    /**
     * ???????????????????????????
     *
     * @param view
     */
    protected void onRightMenuClick(View view) {
        hidePanelAndKeyboard(mPanelRoot);
        ClearHistoryDialog clearHistoryDialog = new ClearHistoryDialog(getSobotActivity());
        clearHistoryDialog.setCanceledOnTouchOutside(true);
        clearHistoryDialog.setOnClickListener(new ClearHistoryDialog.DialogOnClickListener() {
            @Override
            public void onSure() {
                clearHistory();
            }
        });
        clearHistoryDialog.show();
    }

    private SobotClearHistoryMsgDialog clearHistoryMsgDialog;//????????????????????????

    public void clearHistory() {
        if (clearHistoryMsgDialog == null) {
            clearHistoryMsgDialog = new SobotClearHistoryMsgDialog(getSobotActivity(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearHistoryMsgDialog.dismiss();
                    if (v.getId() == ResourceUtils.getResId(getContext(), "sobot_btn_cancle_conversation")) {

                        zhiChiApi.deleteHisMsg(SobotChatFSFragment.this, initModel.getPartnerid(), new StringResultCallBack<CommonModelBase>() {
                            @Override
                            public void onSuccess(CommonModelBase modelBase) {
                                if (!isActive()) {
                                    return;
                                }
                                messageList.clear();
                                cids.clear();
                                messageAdapter.notifyDataSetChanged();
                                lv_message.setPullRefreshEnable(true);// ????????????????????????
                            }

                            @Override
                            public void onFailure(Exception e, String des) {
                            }
                        });
                    } else if (v.getId() == ResourceUtils.getResId(getContext(), "sobot_btn_temporary_leave")) {
                        clearHistoryMsgDialog.dismiss();
                    }
                }
            });
            clearHistoryMsgDialog.show();
        } else {
            clearHistoryMsgDialog.show();
        }
    }

    /**
     * ?????????????????????????????????
     */
    public void hideReLoading() {
        image_reLoading.clearAnimation();
        image_reLoading.setVisibility(View.GONE);
    }

    /**
     * ?????????????????????????????????
     */
    public void resetEmoticonBtn() {
        String panelViewTag = getPanelViewTag(mPanelRoot);
        String instanceTag = CustomeViewFactory.getInstanceTag(mAppContext, btn_emoticon_view.getId());
        if (mPanelRoot.getVisibility() == View.VISIBLE && instanceTag.equals(panelViewTag)) {
            doEmoticonBtn2Focus();
        } else {
            doEmoticonBtn2Blur();
        }
    }

    /**
     * ???????????????????????????
     */
    public void doEmoticonBtn2Focus() {
        btn_emoticon_view.setSelected(true);
    }

    /**
     * ???????????????????????????
     */
    public void doEmoticonBtn2Blur() {
        btn_emoticon_view.setSelected(false);
    }

    /**
     * ????????????????????????????????????
     */
    private void showLeaveMsg() {
        LogUtils.i("???????????????????????????");
        showLogicTitle(getResString("sobot_no_access"), null, false);
        setBottomView(ZhiChiConstant.bottomViewtype_custom_only_msgclose);
        mBottomViewtype = ZhiChiConstant.bottomViewtype_custom_only_msgclose;
        if (isUserBlack()) {
            showCustomerUanbleTip();
        } else {
            showCustomerOfflineTip();
        }
        isSessionOver = true;
    }

    /**
     * ?????????????????????
     *
     * @param item
     */
    @Override
    public void inputEmoticon(EmojiconNew item) {
        InputHelper.input2OSC(et_sendmessage, item);
    }

    /**
     * ????????????????????????
     */
    @Override
    public void backspace() {
        InputHelper.backspace(et_sendmessage);
    }

    /**
     * ????????????????????????????????????
     * ??????
     */
    @Override
    public void btnPicture() {
        hidePanelAndKeyboard(mPanelRoot);
        selectPicFromLocal();
        lv_message.setSelection(messageAdapter.getCount());
    }

    /**
     * ????????????????????????????????????
     * ??????
     */
    @Override
    public void btnVedio() {
        hidePanelAndKeyboard(mPanelRoot);
        selectVedioFromLocal();
        lv_message.setSelection(messageAdapter.getCount());
    }

    /**
     * ????????????????????????????????????
     * ??????
     */
    @Override
    public void btnCameraPicture() {
        hidePanelAndKeyboard(mPanelRoot);
        selectPicFromCamera(); // ?????? ??????
        lv_message.setSelection(messageAdapter.getCount());
    }

    /**
     * ????????????????????????????????????
     * ?????????
     */
    @Override
    public void btnSatisfaction() {
        lv_message.setSelection(messageAdapter.getCount());
        //??????????????? ???????????????????????????????????? ????????? ????????????????????? ????????? ?????????????????????????????????
        submitEvaluation(true, 5, 0, "");
    }

    /**
     * ????????????????????????????????????
     * ????????????
     */
    @Override
    public void chooseFile() {
        if (Build.VERSION.SDK_INT < 30 || CommonUtils.getTargetSdkVersion(getSobotActivity().getApplicationContext()) < 30) {
            if (checkIsShowPermissionPop(getResString("sobot_memory_card"), getResString("sobot_memory_card_yongtu"), 1)) {
                return;
            }
            // ????????????
            if (!checkStoragePermission()) {
                return;
            }
        }
        hidePanelAndKeyboard(mPanelRoot);
        Intent intent = new Intent(getSobotActivity(), SobotChooseFileActivity.class);
        startActivityForResult(intent, ZhiChiConstant.REQUEST_COCE_TO_CHOOSE_FILE);
    }

    @Override
    public void startToPostMsgActivty(final boolean flag_exit_sdk) {
        startToPostMsgActivty(flag_exit_sdk, false);
    }

    /**
     * ??????????????????
     *
     * @param flag_exit_sdk ???????????????????????? ???????????????????????????
     * @param isShowTicket  ????????????????????????  false?????????????????????????????? true???????????????????????????
     */
    public void startToPostMsgActivty(final boolean flag_exit_sdk, final boolean isShowTicket) {
        if (initModel == null) {
            return;
        }

        if (SobotOption.sobotLeaveMsgListener != null) {
            SobotOption.sobotLeaveMsgListener.onLeaveMsg();
            return;
        }
        hidePanelAndKeyboard();
        if (initModel.isMsgToTicketFlag()) {
            Intent intent = SobotPostLeaveMsgActivity.newIntent(getContext(), initModel.getMsgLeaveTxt()
                    , initModel.getMsgLeaveContentTxt(), initModel.getPartnerid());
            startActivityForResult(intent, SobotPostLeaveMsgActivity.EXTRA_MSG_LEAVE_REQUEST_CODE);
        } else {
            String tempGroupId = SharedPreferencesUtil.getStringData(getSobotActivity(), ZhiChiConstant.sobot_connect_group_id, "");
            mPostMsgPresenter.obtainTemplateList(initModel.getPartnerid(), tempGroupId, flag_exit_sdk, isShowTicket, new StPostMsgPresenter.ObtainTemplateListDelegate() {
                @Override
                public void onSuccess(Intent intent) {
                    intent.putExtra(StPostMsgPresenter.INTENT_KEY_COMPANYID, initModel.getCompanyId());
                    intent.putExtra(StPostMsgPresenter.INTENT_KEY_CUSTOMERID, initModel.getCustomerId());
                    intent.putExtra(ZhiChiConstant.FLAG_EXIT_SDK, flag_exit_sdk);
                    intent.putExtra(StPostMsgPresenter.INTENT_KEY_GROUPID, info.getGroupid());
                    intent.putExtra(StPostMsgPresenter.INTENT_KEY_IS_SHOW_TICKET, isShowTicket);
                    startActivity(intent);
                    if (getSobotActivity() != null) {
                        getSobotActivity().overridePendingTransition(ResourceUtils.getIdByName(mAppContext, "anim", "sobot_push_left_in"),
                                ResourceUtils.getIdByName(mAppContext, "anim", "sobot_push_left_out"));
                    }
                }
            });
        }
    }

    /**
     * ????????????????????????
     */
    public void switchEmoticonBtn() {
        boolean flag = btn_emoticon_view.isSelected();
        if (flag) {
            doEmoticonBtn2Blur();
        } else {
            doEmoticonBtn2Focus();
        }
        //?????????????????????
        if (btn_emoticon_view.isSelected()) {
            btn_emoticon_view.setBackgroundResource(ResourceUtils.getDrawableId(getContext(), "sobot_keyboard_normal"));
        } else {
            btn_emoticon_view.setBackgroundResource(ResourceUtils.getDrawableId(getContext(), "sobot_emoticon_button_selector"));
        }
    }

    //??????????????????????????????
    public void switchPanelAndKeyboard(final View panelLayout, final View switchPanelKeyboardBtn, final View focusView) {
        if (currentPanelId == 0 || currentPanelId == switchPanelKeyboardBtn.getId()) {
            //????????????????????????  ???????????????????????????????????????????????????
            boolean switchToPanel = panelLayout.getVisibility() != View.VISIBLE;
            if (!switchToPanel) {
                KPSwitchConflictUtil.showKeyboard(panelLayout, focusView);
            } else {
                KPSwitchConflictUtil.showPanel(panelLayout);
                setPanelView(panelLayout, switchPanelKeyboardBtn.getId());
            }
        } else {
            //????????????  ??????????????????????????????????????????  ?????????????????????
            KPSwitchConflictUtil.showPanel(panelLayout);
            setPanelView(panelLayout, switchPanelKeyboardBtn.getId());
        }
        currentPanelId = switchPanelKeyboardBtn.getId();
    }

    /*
     * ??????????????????????????????   ????????????????????????????????????????????? ????????????????????????
     * ??????????????????????????????
     */
    public void pressSpeakSwitchPanelAndKeyboard(final View switchPanelKeyboardBtn) {
        if (btn_press_to_speak.isShown()) {
            btn_model_edit.setVisibility(View.GONE);
            showVoiceBtn();
            btn_press_to_speak.setVisibility(View.GONE);
            edittext_layout.setVisibility(View.VISIBLE);

            et_sendmessage.requestFocus();
            KPSwitchConflictUtil.showPanel(mPanelRoot);
            setPanelView(mPanelRoot, switchPanelKeyboardBtn.getId());
            currentPanelId = switchPanelKeyboardBtn.getId();
        } else {
            //???????????????????????????
            switchPanelAndKeyboard(mPanelRoot, switchPanelKeyboardBtn, et_sendmessage);
        }
    }

    /**
     * ?????????????????????view
     *
     * @param panelLayout
     * @param btnId
     */
    private void setPanelView(final View panelLayout, int btnId) {
        if (panelLayout instanceof KPSwitchFSPanelLinearLayout) {
            KPSwitchFSPanelLinearLayout tmpView = (KPSwitchFSPanelLinearLayout) panelLayout;
            View childView = tmpView.getChildAt(0);
            if (childView != null && childView instanceof CustomeChattingPanel) {
                CustomeChattingPanel customeChattingPanel = (CustomeChattingPanel) childView;
                Bundle bundle = new Bundle();
                bundle.putInt("current_client_model", current_client_model);
                customeChattingPanel.setupView(btnId, bundle, SobotChatFSFragment.this);
            }
        }
    }

    /**
     * ????????????????????????????????????tag
     *
     * @param panelLayout
     */
    private String getPanelViewTag(final View panelLayout) {
        String str = "";
        if (panelLayout instanceof KPSwitchFSPanelLinearLayout) {
            KPSwitchFSPanelLinearLayout tmpView = (KPSwitchFSPanelLinearLayout) panelLayout;
            View childView = tmpView.getChildAt(0);
            if (childView != null && childView instanceof CustomeChattingPanel) {
                CustomeChattingPanel customeChattingPanel = (CustomeChattingPanel) childView;
                str = customeChattingPanel.getPanelViewTag();
            }
        }
        return str;
    }

    /**
     * ?????????????????????
     *
     * @param layout
     */
    public void hidePanelAndKeyboard(KPSwitchFSPanelLinearLayout layout) {
        if (layout != null) {
            layout.setVisibility(View.GONE);
        }
        et_sendmessage.dismissPop();
        KPSwitchConflictUtil.hidePanelAndKeyboard(layout);
        doEmoticonBtn2Blur();
        currentPanelId = 0;
    }

    /*
     * ????????????
     */
    private void showHint(String content) {
        ZhiChiMessageBase zhichiMessageBase = new ZhiChiMessageBase();
        ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
        zhichiMessageBase.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");
        reply.setMsg(content);
        reply.setRemindType(ZhiChiConstant.sobot_remind_type_tip);
        zhichiMessageBase.setAnswer(reply);
        zhichiMessageBase.setAction(ZhiChiConstant.action_remind_no_service);
        updateUiMessage(messageAdapter, zhichiMessageBase);
    }

    @Override
    public void onRobotGuessComplete(String question) {
        //???????????? ????????????
        et_sendmessage.setText("");
        sendMsg(question);
    }

    @Override
    public void onRefresh() {
        getHistoryMessage(false);
    }

    /**
     * ??????????????????
     *
     * @param isFirst ???????????????????????????
     */
    public void getHistoryMessage(final boolean isFirst) {
        if (initModel == null)
            return;

        if (queryCidsStatus == ZhiChiConstant.QUERY_CIDS_STATUS_INITIAL || queryCidsStatus == ZhiChiConstant.QUERY_CIDS_STATUS_FAILURE) {
            //cid?????????????????????????????????????????????????????????cid
            onLoad();
            queryCids();
        } else if ((queryCidsStatus == ZhiChiConstant.QUERY_CIDS_STATUS_LOADING && !isFirst) || isInGethistory) {
            //1.??????cid??????????????? ????????????????????????????????????  ?????? ??????????????????????????????
            //2.??????????????????????????????????????????   ?????????????????????
            onLoad();
        } else {
            String currentCid = ChatUtils.getCurrentCid(initModel, cids, currentCidPosition);
            if ("-1".equals(currentCid)) {
                showNoHistory();
                onLoad();
                return;
            }
            isInGethistory = true;
            zhiChiApi.getChatDetailByCid(SobotChatFSFragment.this, initModel.getPartnerid(), currentCid, new StringResultCallBack<ZhiChiHistoryMessage>() {
                @Override
                public void onSuccess(ZhiChiHistoryMessage zhiChiHistoryMessage) {
                    isInGethistory = false;
                    if (!isActive()) {
                        return;
                    }
                    onLoad();
                    currentCidPosition++;
                    List<ZhiChiHistoryMessageBase> data = zhiChiHistoryMessage.getData();
                    if (data != null && data.size() > 0) {
                        showData(data);
                    } else {
                        //??????????????????????????????
                        getHistoryMessage(false);
                    }
                }

                @Override
                public void onFailure(Exception e, String des) {
                    isInGethistory = false;
                    if (!isActive()) {
                        return;
                    }
                    mUnreadNum = 0;
                    updateFloatUnreadIcon();
                    onLoad();
                }
            });
        }
    }

    private void showData(List<ZhiChiHistoryMessageBase> result) {
        List<ZhiChiMessageBase> msgLists = new ArrayList<>();
        List<ZhiChiMessageBase> msgList;
        for (int i = 0; i < result.size(); i++) {
            ZhiChiHistoryMessageBase historyMsg = result.get(i);
            msgList = historyMsg.getContent();

            for (ZhiChiMessageBase base : msgList) {
                base.setSugguestionsFontColor(1);
                if ((ZhiChiConstant.message_type_fraud_prevention + "").equals(base.getAction())) {
                } else {
                    if (base.getSdkMsg() != null) {
                        ZhiChiReplyAnswer answer = base.getSdkMsg().getAnswer();
                        if (answer != null) {
                            if (answer.getMsgType() == null) {
                                answer.setMsgType("0");
                            }

                            if (!TextUtils.isEmpty(answer.getMsg()) && answer.getMsg().length() > 4) {
                                String msg = answer.getMsg().replace("&lt;/p&gt;", "<br>");
                                if (msg.endsWith("<br>")) {
                                    msg = msg.substring(0, msg.length() - 4);
                                }
                                answer.setMsg(msg);
                            }
                        }
                        if (TextUtils.isEmpty(base.getSenderType())) {
                            base = null;
                            continue;
                        }
                        if (ZhiChiConstant.message_sender_type_robot == Integer
                                .parseInt(base.getSenderType())) {
                            base.setSenderName(TextUtils.isEmpty(base.getSenderName()) ? initModel
                                    .getRobotName() : base.getSenderName());
                            base.setSenderFace(TextUtils.isEmpty(base.getSenderFace()) ? initModel
                                    .getRobotLogo() : base.getSenderFace());
                        }
                        base.setAnswer(answer);
                        base.setAnswerType(base.getSdkMsg()
                                .getAnswerType());
                    }
                }
            }
            msgLists.addAll(msgList);
        }

        if (msgLists.size() > 0) {
            if (mUnreadNum > 0) {
                ZhiChiMessageBase unreadMsg = ChatUtils.getUnreadMode(mAppContext);
                unreadMsg.setCid(msgLists.get(msgLists.size() - 1).getCid());
                msgLists.add((msgLists.size() - mUnreadNum) < 0 ? 0 : (msgLists.size() - mUnreadNum)
                        , unreadMsg);
                updateFloatUnreadIcon();
                mUnreadNum = 0;
            }
            messageAdapter.addData(msgLists);
            messageAdapter.notifyDataSetChanged();
            lv_message.setSelection(msgLists.size());
        }
    }

    /**
     * ??????????????????????????????
     */
    private void showNoHistory() {
        ZhiChiMessageBase base = new ZhiChiMessageBase();

        base.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");

        ZhiChiReplyAnswer reply1 = new ZhiChiReplyAnswer();
        reply1.setRemindType(ZhiChiConstant.sobot_remind_type_nomore);
        reply1.setMsg("");
        base.setAnswer(reply1);
        // ?????????????????????
        updateUiMessageBefore(messageAdapter, base);
        lv_message.setSelection(0);

        lv_message.setPullRefreshEnable(false);// ????????????????????????
        isNoMoreHistoryMsg = true;
        mUnreadNum = 0;
    }

    private void onLoad() {
        lv_message.onRefreshCompleteHeader();
    }

    // ???????????????????????????????????????
    private void editModelToVoice(int typeModel, String str) {
        btn_model_edit.setVisibility(View.GONE == typeModel ? View.GONE
                : View.VISIBLE); // ??????????????????
        btn_model_voice.setVisibility(View.VISIBLE != typeModel ? View.VISIBLE
                : View.GONE);// ??????????????????
        btn_press_to_speak.setVisibility(View.GONE != typeModel ? View.VISIBLE
                : View.GONE);
        edittext_layout.setVisibility(View.VISIBLE == typeModel ? View.GONE
                : View.VISIBLE);

        if (!TextUtils.isEmpty(et_sendmessage.getText().toString()) && str.equals("123")) {
            btn_send.setVisibility(View.VISIBLE);
            btn_upload_view.setVisibility(View.GONE);
        } else {
            btn_send.setVisibility(View.GONE);
            btn_upload_view.setVisibility(View.VISIBLE);
        }
    }

    public void setShowNetRemind(boolean isShow) {
        net_status_remide.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * ??????????????????
     */
    public class MyMessageReceiver extends BroadcastReceiver {
        @SuppressWarnings("deprecation")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                if (!CommonUtils.isNetWorkConnected(mAppContext)) {
                    //????????????
                    if (welcome.getVisibility() != View.VISIBLE) {
                        setShowNetRemind(true);
                    }
                } else {
                    // ?????????
                    setShowNetRemind(false);
                }
            }
        }
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                LogUtils.i("?????????  :" + intent.getAction());
                if (ZhiChiConstants.receiveMessageBrocast.equals(intent.getAction())) {
                    // ?????????????????????
                    ZhiChiPushMessage pushMessage = null;
                    try {
                        Bundle extras = intent.getExtras();
                        if (extras != null) {
                            pushMessage = (ZhiChiPushMessage) extras.getSerializable(ZhiChiConstants.ZHICHI_PUSH_MESSAGE);
                            LogUtils.i("???????????????  :" + pushMessage.toString());
                        }
                    } catch (Exception e) {
                        //ignor
                    }
                    if (pushMessage == null || !info.getApp_key().equals(pushMessage.getAppId())) {
                        return;
                    }
                    ZhiChiMessageBase base = new ZhiChiMessageBase();

                    //??????????????????????????????????????????
                    if (ZhiChiConstant.push_message_receverSystemMessage == pushMessage
                            .getType()) {// ??????????????????
                        if (customerState == CustomerState.Online) {
                            base.setT(Calendar.getInstance().getTime().getTime() + "");
                            base.setMsgId(pushMessage.getMsgId());
                            base.setSender(pushMessage.getAname());
                            base.setSenderName(pushMessage.getAname());
                            base.setSenderFace(pushMessage.getAface());
                            if (!TextUtils.isEmpty(pushMessage.getSysType()) && ("1".equals(pushMessage.getSysType()) || "2".equals(pushMessage.getSysType()))) {
                                //?????????????????? 1
                                //?????????????????? 2 ??????????????????
                                base.setSenderType(ZhiChiConstant.message_sender_type_service + "");
                                ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
                                reply.setMsg(pushMessage.getContent());
                                reply.setMsgType(ZhiChiConstant.message_type_text + "");
                                base.setAnswer(reply);
                            } else {
                                base.setAction(ZhiChiConstant.message_type_fraud_prevention + "");
                                base.setMsgId(pushMessage.getMsgId());
                                base.setMsg(pushMessage.getContent());
                                stopCustomTimeTask();
                                startUserInfoTimeTask(handler);
                            }
                            // ?????????????????????
                            messageAdapter.justAddData(base);
                            messageAdapter.notifyDataSetChanged();
                            ChatUtils.msgLogicalProcess(initModel, messageAdapter, pushMessage);
                            messageAdapter.notifyDataSetChanged();
                            return;
                        }
                    }

                    base.setT(Calendar.getInstance().getTime().getTime() + "");
                    base.setMsgId(pushMessage.getMsgId());
                    base.setSender(pushMessage.getAname());
                    base.setSenderName(pushMessage.getAname());
                    base.setSenderFace(pushMessage.getAface());
                    base.setOrderCardContent(pushMessage.getOrderCardContent());
                    base.setConsultingContent(pushMessage.getConsultingContent());
                    base.setSenderType(ZhiChiConstant.message_sender_type_service + "");
                    base.setAnswer(pushMessage.getAnswer());

                    if (ZhiChiConstant.push_message_createChat == pushMessage.getType()) {
                        setAdminFace(pushMessage.getAface());
                        createCustomerService(pushMessage.getAname(), pushMessage.getAface());
                    } else if (ZhiChiConstant.push_message_paidui == pushMessage.getType()) {
                        // ?????????????????????
                        createCustomerQueue(pushMessage.getCount(), 0, pushMessage.getQueueDoc(), isShowQueueTip);
                    } else if (ZhiChiConstant.push_message_receverNewMessage == pushMessage.getType()) {
                        // ?????????????????????
                        base.setMsgId(pushMessage.getMsgId());
                        base.setSender(pushMessage.getAname());
                        base.setSenderName(pushMessage.getAname());
                        base.setSenderFace(pushMessage.getAface());
                        base.setSenderType(ZhiChiConstant.message_sender_type_service + "");
                        base.setAnswer(pushMessage.getAnswer());
                        stopCustomTimeTask();
                        startUserInfoTimeTask(handler);
                        // ?????????????????????
                        messageAdapter.justAddData(base);
                        messageAdapter.notifyDataSetChanged();
                        ChatUtils.msgLogicalProcess(initModel, messageAdapter, pushMessage);
                        messageAdapter.notifyDataSetChanged();
                        //???????????????????????????
                        customerState = CustomerState.Online;
                    } else if (ZhiChiConstant.push_message_outLine == pushMessage.getType() && customerState == CustomerState.Online) {
                        if (6 == Integer.parseInt(pushMessage.getStatus())) {
                            // ??????????????? ????????????
                            String puid = SharedPreferencesUtil.getStringData(getSobotActivity(), Const.SOBOT_PUID, "");
                            if (!TextUtils.isEmpty(puid) && !TextUtils.isEmpty(pushMessage.getPuid()) && puid.equals(pushMessage.getPuid())) {
                                customerServiceOffline(initModel, Integer.parseInt(pushMessage.getStatus()));
                            }
                        } else {
                            // ???????????????
                            customerServiceOffline(initModel, Integer.parseInt(pushMessage.getStatus()));
                        }
                    } else if (ZhiChiConstant.push_message_transfer == pushMessage.getType()) {
                        LogUtils.i("???????????????--->" + pushMessage.getName());
                        //???????????? ????????????????????????face ???name
                        showLogicTitle(pushMessage.getName(), pushMessage.getFace(), false);
                        setAdminFace(pushMessage.getFace());
                        currentUserName = pushMessage.getName();
                    } else if (ZhiChiConstant.push_message_user_get_session_lock_msg == pushMessage.getType()) {
                        if (customerState == CustomerState.Online) {
                            //1 ????????????
                            if (1 == pushMessage.getLockType()) {
                                paseReplyTimeCustoms = noReplyTimeCustoms;
                                paseReplyTimeUserInfo = noReplyTimeUserInfo;
                                isChatLock = 1;
                                if (is_startCustomTimerTask) {
                                    LogUtils.i("?????????????????? ??????--->" + noReplyTimeCustoms);
                                    stopCustomTimeTask();
                                    is_startCustomTimerTask = true;
                                    //????????????????????????????????????????????????,????????????0???
                                    noReplyTimeCustoms = paseReplyTimeCustoms;
                                    customTimeTask = true;
                                } else {
                                    LogUtils.i("?????????????????? ??????--->" + noReplyTimeUserInfo);
                                    stopUserInfoTimeTask();
                                    noReplyTimeUserInfo = paseReplyTimeUserInfo;
                                    userInfoTimeTask = true;
                                }
                            } else {
                                isChatLock = 2;
                                //2 ????????????
                                if (current_client_model == ZhiChiConstant.client_model_customService) {
                                    if (is_startCustomTimerTask) {
                                        stopCustomTimeTask();
                                        startCustomTimeTask(handler);
                                        //????????????????????????????????????????????????,????????????0???
                                        noReplyTimeCustoms = paseReplyTimeCustoms;
                                        customTimeTask = true;
                                        LogUtils.i("?????????????????? ??????--->" + noReplyTimeCustoms);
                                    } else {
                                        stopUserInfoTimeTask();
                                        startUserInfoTimeTask(handler);
                                        userInfoTimeTask = true;
                                        noReplyTimeUserInfo = paseReplyTimeUserInfo;
                                        LogUtils.i("?????????????????? ??????--->" + noReplyTimeUserInfo);
                                    }
                                }
                            }
                        }
                    } else if (ZhiChiConstant.push_message_custom_evaluate == pushMessage.getType()) {
                        LogUtils.i("???????????????????????????.................");
                        //?????????????????????
                        if (isAboveZero && !isComment && customerState == CustomerState.Online) {
                            // ?????????????????????????????????????????????????????? ?????? ????????????
                            ZhiChiMessageBase customEvaluateMode = ChatUtils.getCustomEvaluateMode(pushMessage);
                            // ?????????????????????
                            updateUiMessage(messageAdapter, customEvaluateMode);
                        }
                    } else if (ZhiChiConstant.push_message_retracted == pushMessage.getType()) {
                        if (!TextUtils.isEmpty(pushMessage.getRevokeMsgId())) {
                            List<ZhiChiMessageBase> datas = messageAdapter.getDatas();
                            for (int i = datas.size() - 1; i >= 0; i--) {
                                ZhiChiMessageBase msgData = datas.get(i);
                                if (pushMessage.getRevokeMsgId().equals(msgData.getMsgId())) {
                                    if (!msgData.isRetractedMsg()) {
                                        msgData.setRetractedMsg(true);
                                        messageAdapter.notifyDataSetChanged();
                                    }
                                    break;
                                }
                            }
                        }
                    }
                } else if (ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_LOCATION.equals(intent.getAction())) {
                    SobotLocationModel locationData = (SobotLocationModel) intent.getSerializableExtra(ZhiChiConstant.SOBOT_LOCATION_DATA);
                    if (locationData != null) {
                        sendLocation(null, locationData, handler, true);
                    }
                } else if (ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_TEXT.equals(intent.getAction())) {
                    String content = intent.getStringExtra(ZhiChiConstant.SOBOT_SEND_DATA);
                    String sendTextTo = intent.getStringExtra("sendTextTo");
                    if (ZhiChiConstant.client_model_robot == current_client_model && "robot".equals(sendTextTo)) { // ??????????????????????????????
                        if (!TextUtils.isEmpty(content)) {
                            sendMsg(content);
                        }
                    } else if (ZhiChiConstant.client_model_customService == current_client_model && "user".equals(sendTextTo)) {
                        if (!TextUtils.isEmpty(content)) {
                            sendMsg(content);
                        }
                    }
                } else if (ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_OBJECT.equals(intent.getAction())) {
                    String content = intent.getStringExtra(ZhiChiConstant.SOBOT_SEND_DATA);
                    String type = intent.getStringExtra(ZhiChiConstant.SOBOT_TYPE_DATA);
                    if (ZhiChiConstant.client_model_customService == current_client_model) {
                        if (TextUtils.isEmpty(content)) {
                            LogUtils.i("????????????????????????");
                            return;
                        }
                        if ("0".equals(type)) {
                            //????????????
                            sendMsg(content);
                        } else if ("1".equals(type)) {
                            //????????????
                            File sendFile = new File(content);
                            if (sendFile.exists()) {
                                uploadFile(sendFile, handler, lv_message, messageAdapter, false);
                            }
                        } else if ("3".equals(type)) {
                            //????????????
                            File sendFile = new File(content);
                            if (sendFile.exists()) {
                                uploadVideo(sendFile, null, messageAdapter);
                            }
                        } else if ("4".equals(type)) {
                            //????????????
                            File sendFile = new File(content);
                            if (sendFile.exists()) {
                                uploadFile(sendFile, handler, lv_message, messageAdapter, false);
                            }
                        }
                    }
                } else if (ZhiChiConstant.SOBOT_BROCAST_ACTION_TRASNFER_TO_OPERATOR.equals(intent.getAction())) {
                    //?????????????????????
                    SobotTransferOperatorParam transferParam = (SobotTransferOperatorParam) intent.getSerializableExtra(ZhiChiConstant.SOBOT_SEND_DATA);
                    if (transferParam != null) {
                        if (transferParam.getConsultingContent() != null) {
                            info.setConsultingContent(transferParam.getConsultingContent());
                        }
                        if (transferParam.getSummary_params() != null) {
                            info.setSummary_params(transferParam.getSummary_params());
                        }
                        SobotConnCusParam param = new SobotConnCusParam();
                        param.setGroupId(transferParam.getGroupId());
                        param.setGroupName(transferParam.getGroupName());
                        param.setKeyword(transferParam.getKeyword());
                        param.setKeywordId(transferParam.getKeywordId());
                        connectCustomerService(param, transferParam.isShowTips());
                    }
                } else if (ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_CARD.equals(intent.getAction())) {
                    ConsultingContent consultingContent = (ConsultingContent) intent.getSerializableExtra(ZhiChiConstant.SOBOT_SEND_DATA);
                    sendCardMsg(consultingContent);
                } else if (ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_ORDER_CARD.equals(intent.getAction())) {
                    OrderCardContentModel orderCardContent = (OrderCardContentModel) intent.getSerializableExtra(ZhiChiConstant.SOBOT_SEND_DATA);
                    sendOrderCardMsg(orderCardContent);
                }

                if (ZhiChiConstants.chat_remind_post_msg.equals(intent.getAction())) {
                    boolean isShowTicket = intent.getBooleanExtra("isShowTicket", false);
                    if (isShowTicket) {
                        for (int i = messageList.size() - 1; i > 0; i--) {
                            if (!TextUtils.isEmpty(messageList.get(i).getSenderType()) && Integer.parseInt(messageList.get(i).getSenderType()) == ZhiChiConstant.message_sender_type_remide_info
                                    && messageList.get(i).getAnswer() != null
                                    && ZhiChiConstant.sobot_remind_type_simple_tip == messageList.get(i).getAnswer().getRemindType()) {
                                messageList.remove(i);
                                messageAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                        Intent intent2 = mPostMsgPresenter.newPostMsgIntent(initModel.getUid(), null);
                        intent2.putExtra(StPostMsgPresenter.INTENT_KEY_COMPANYID, initModel.getCompanyId());
                        intent2.putExtra(StPostMsgPresenter.INTENT_KEY_CUSTOMERID, initModel.getCustomerId());
                        intent2.putExtra(ZhiChiConstant.FLAG_EXIT_SDK, false);
                        intent2.putExtra(StPostMsgPresenter.INTENT_KEY_GROUPID, info.getSkillSetId());
                        intent2.putExtra(StPostMsgPresenter.INTENT_KEY_IS_SHOW_TICKET, true);
                        startActivity(intent2);
                        if (getSobotActivity() != null) {
                            getSobotActivity().overridePendingTransition(ResourceUtils.getIdByName(mAppContext, "anim", "sobot_push_left_in"),
                                    ResourceUtils.getIdByName(mAppContext, "anim", "sobot_push_left_out"));
                        }
                    } else {
                        startToPostMsgActivty(false, false);
                    }

                } else if (ZhiChiConstants.sobot_click_cancle.equals(intent.getAction())) {
                    //?????????????????????????????????
                    if (type == ZhiChiConstant.type_custom_first && current_client_model ==
                            ZhiChiConstant.client_model_robot) {
                        remindRobotMessage(handler, initModel, info);
                    }
                } else if (ZhiChiConstants.chat_remind_to_customer.equals(intent.getAction())) {
                    //?????????
                    doClickTransferBtn();
                }else if (ZhiChiConstants.SOBOT_POST_MSG_TMP_BROCAST.equals(intent.getAction())) {
                    //??????????????????????????????????????????
                    Intent postMsgIntent = new Intent(getContext(), SobotPostMsgActivity.class);
                    postMsgIntent.putExtra("intent_key_uid", intent.getStringExtra("uid"));
                    postMsgIntent.putExtra("intent_key_config", intent.getSerializableExtra("sobotLeaveMsgConfig"));
                    postMsgIntent.putExtra(StPostMsgPresenter.INTENT_KEY_COMPANYID, initModel.getCompanyId());
                    postMsgIntent.putExtra(StPostMsgPresenter.INTENT_KEY_CUSTOMERID, initModel.getCustomerId());
                    postMsgIntent.putExtra(ZhiChiConstant.FLAG_EXIT_SDK, intent.getBooleanExtra("mflag_exit_sdk", false));
                    postMsgIntent.putExtra(StPostMsgPresenter.INTENT_KEY_GROUPID, info.getGroupid());
                    postMsgIntent.putExtra(StPostMsgPresenter.INTENT_KEY_IS_SHOW_TICKET, intent.getBooleanExtra("mIsShowTicket", false));
                    startActivity(postMsgIntent);
                    if (getSobotActivity() != null) {
                        getSobotActivity().overridePendingTransition(ResourceUtils.getIdByName(mAppContext, "anim", "sobot_push_left_in"),
                                ResourceUtils.getIdByName(mAppContext, "anim", "sobot_push_left_out"));
                    }
                }  else if (ZhiChiConstants.dcrc_comment_state.equals(intent.getAction())) {
                    //???????????????????????????????????????
                    isComment = intent.getBooleanExtra("commentState", false);
                    boolean isFinish = intent.getBooleanExtra("isFinish", false);
                    boolean isExitSession = intent.getBooleanExtra("isExitSession", false);
                    int commentType = intent.getIntExtra("commentType", 1);

                    //????????????????????? ??????ui
                    int score = intent.getIntExtra("score", 5);
                    int isResolved = intent.getIntExtra("isResolved", 0);
//                messageAdapter.submitEvaluateData(isResolved, score);
                    messageAdapter.removeEvaluateData();
                    messageAdapter.notifyDataSetChanged();
//                refreshItemByCategory(CusEvaluateMessageHolder.class);

                    if (isExitSession || ChatUtils.isEvaluationCompletedExit(mAppContext, isComment, current_client_model)) {
                        //????????????????????????????????????????????????
                        isSessionOver = true;
                        customerServiceOffline(initModel, 5);
                        ChatUtils.userLogout(mAppContext);
                    }
                    if (isActive()) {
                        ChatUtils.showThankDialog(getSobotActivity(), handler, isFinish);
                    }
                } else if (ZhiChiConstants.sobot_close_now.equals(intent.getAction())) {
                    if (intent.getBooleanExtra("isBackShowEvaluate", true)) {
                        //????????? ?????? ????????????????????? ???????????????????????????
                        finish();
                    } else {
                        //???????????????????????????????????? ???????????????????????????
                        customerServiceOffline(initModel, 1);
                        ChatUtils.userLogout(mAppContext);
                        finish();
                    }
                } else if (ZhiChiConstants.sobot_close_now_clear_cache.equals(intent.getAction())) {
                    isSessionOver = true;
                    finish();
                } else if (ZhiChiConstants.SOBOT_CHANNEL_STATUS_CHANGE.equals(intent.getAction())) {
                    if (customerState == CustomerState.Online || customerState == CustomerState.Queuing) {
                        int connStatus = intent.getIntExtra("connStatus", Const.CONNTYPE_IN_CONNECTION);
                        LogUtils.i("connStatus:" + connStatus);
                        switch (connStatus) {
                            case Const.CONNTYPE_IN_CONNECTION:
                                sobot_container_conn_status.setVisibility(View.VISIBLE);
                                sobot_title_conn_status.setText(getResString("sobot_conntype_in_connection"));
                                if (sobot_header_center_ll != null) {
                                    sobot_header_center_ll.setVisibility(View.GONE);
                                }
                                sobot_conn_loading.setVisibility(View.VISIBLE);
                                break;
                            case Const.CONNTYPE_CONNECT_SUCCESS:
                                setShowNetRemind(false);
                                sobot_container_conn_status.setVisibility(View.GONE);
                                sobot_title_conn_status.setText("");
                                if (sobot_header_center_ll != null) {
                                    sobot_header_center_ll.setVisibility(View.VISIBLE);
                                }
                                sobot_conn_loading.setVisibility(View.GONE);
                                stopPolling();
                                break;
                            case Const.CONNTYPE_UNCONNECTED:
                                sobot_container_conn_status.setVisibility(View.VISIBLE);
                                sobot_title_conn_status.setText(getResString("sobot_conntype_unconnected"));
                                if (sobot_header_center_ll != null) {
                                    sobot_header_center_ll.setVisibility(View.GONE);
                                }
                                sobot_conn_loading.setVisibility(View.GONE);
                                if (welcome.getVisibility() != View.VISIBLE) {
                                    setShowNetRemind(true);
                                }
                                break;
                        }
                    } else {
                        mTitleTextView.setVisibility(View.GONE);
                        mAvatarIV.setVisibility(View.VISIBLE);
                        sobot_container_conn_status.setVisibility(View.GONE);
                    }
                } else if (ZhiChiConstants.SOBOT_BROCAST_KEYWORD_CLICK.equals(intent.getAction())) {
                    String tempGroupId = intent.getStringExtra("tempGroupId");
                    String keyword = intent.getStringExtra("keyword");
                    String keywordId = intent.getStringExtra("keywordId");
                    transfer2Custom(tempGroupId, keyword, keywordId, true);
                } else if (ZhiChiConstants.SOBOT_BROCAST_REMOVE_FILE_TASK.equals(intent.getAction())) {
                    try {
                        String msgId = intent.getStringExtra("sobot_msgId");
                        if (!TextUtils.isEmpty(msgId)) {
                            for (int i = messageList.size() - 1; i >= 0; i--) {
                                if (msgId.equals(messageList.get(i).getId())) {
                                    messageList.remove(i);
                                    break;
                                }
                            }
                            messageAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        //ignor
                    }
                }

            } catch (Exception e) {

            }
        }
    }

    //??????????????????????????????????????????
    private void saveCache() {
        ZhiChiConfig config = SobotMsgManager.getInstance(mAppContext).getConfig(info.getApp_key());
        config.isShowUnreadUi = true;
        config.setMessageList(messageList);
        config.setInitModel(initModel);
        config.current_client_model = current_client_model;
        if (queryCidsStatus == ZhiChiConstant.QUERY_CIDS_STATUS_SUCCESS) {
            config.cids = cids;
            config.currentCidPosition = currentCidPosition;
            config.queryCidsStatus = queryCidsStatus;
        }

        config.activityTitle = getActivityTitle();
        config.customerState = customerState;
        config.remindRobotMessageTimes = remindRobotMessageTimes;
        config.isAboveZero = isAboveZero;
        config.isComment = isComment;
        config.adminFace = getAdminFace();
        config.paseReplyTimeCustoms = noReplyTimeCustoms;
        config.customTimeTask = customTimeTask;
        config.paseReplyTimeUserInfo = noReplyTimeUserInfo;
        config.userInfoTimeTask = userInfoTimeTask;
        config.isChatLock = isChatLock;
        config.currentUserName = currentUserName;
        config.isNoMoreHistoryMsg = isNoMoreHistoryMsg;
        config.showTimeVisiableCustomBtn = showTimeVisiableCustomBtn;
        config.bottomViewtype = mBottomViewtype;
        config.queueNum = queueNum;
        config.isShowQueueTip = isShowQueueTip;
        config.tempMsgContent = tempMsgContent;
        config.inPolling = inPolling;

        if (config.isChatLock == 2 || config.isChatLock == 0) {
            Intent intent = new Intent();
            intent.setAction(ZhiChiConstants.SOBOT_TIMER_BROCAST);
            intent.putExtra("info", info);
            intent.putExtra("isStartTimer", true);
            localBroadcastManager.sendBroadcast(intent);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == notReadInfo) {
            for (int i = messageList.size() - 1; i >= 0; i--) {
                if (messageList.get(i).getAnswer() != null && ZhiChiConstant.
                        sobot_remind_type_below_unread == messageList.get(i).getAnswer().getRemindType()) {
                    lv_message.setSelection(i);
                    break;
                }
            }
            notReadInfo.setVisibility(View.GONE);
        }

        if (view == btn_send) {// ??????????????????
            //??????????????????
            final String message_result = et_sendmessage.getText().toString().trim();
            if (!TextUtils.isEmpty(message_result) && !isConnCustomerService) {
                //?????????????????????????????????  ???????????????????????????????????????
                resetEmoticonBtn();
                try {
                    et_sendmessage.setText("");
                    sendMsg(message_result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (view == btn_upload_view) {
            LogUtils.i("-------????????????-------");
            pressSpeakSwitchPanelAndKeyboard(btn_upload_view);
            doEmoticonBtn2Blur();
            gotoLastItem();
        }

        if (view == btn_emoticon_view) {//??????????????????
            // ??????????????????
            pressSpeakSwitchPanelAndKeyboard(btn_emoticon_view);
            //???????????????????????????
            switchEmoticonBtn();
            gotoLastItem();
        }

        if (view == btn_model_edit) {// ??????????????????????????????
            hideRobotVoiceHint();
            doEmoticonBtn2Blur();
            // ??????????????????
            KPSwitchConflictUtil.showKeyboard(mPanelRoot, et_sendmessage);
            editModelToVoice(View.GONE, "123");// ?????????????????? ?????????????????????
        }

        if (view == btn_model_voice) { // ??????????????????????????????
            showRobotVoiceHint();
            doEmoticonBtn2Blur();
            permissionListener = new PermissionListenerImpl() {
                @Override
                public void onPermissionSuccessListener() {
                    showAudioRecorder();
                }
            };
            if (checkIsShowPermissionPop(getResString("sobot_microphone"), getResString("sobot_microphone_yongtu"), 2)) {
                return;
            }
            if (!checkAudioPermission()) {
                return;
            }
            showAudioRecorder();
        }

        if (view == sobot_ll_switch_robot) {
            // ???????????????????????????
            if (!isSessionOver && (mRobotListDialog == null || !mRobotListDialog.isShowing())) {
                mRobotListDialog = ChatUtils.showRobotListDialog(getSobotActivity(), initModel, this);
            }
        }

        if (view == sobot_tv_right_second) {
            if (!TextUtils.isEmpty(SobotUIConfig.sobot_title_right_menu2_call_num)) {
                if (SobotOption.functionClickListener != null) {
                    SobotOption.functionClickListener.onClickFunction(getSobotActivity(), SobotFunctionType.ZC_PhoneCustomerService);
                }
                ChatUtils.callUp(SobotUIConfig.sobot_title_right_menu2_call_num, getContext());
            } else {
                btnSatisfaction();
            }
        }

        if (view == sobot_tv_right_third) {
            if (!TextUtils.isEmpty(SobotUIConfig.sobot_title_right_menu3_call_num)) {
                if (SobotOption.functionClickListener != null) {
                    SobotOption.functionClickListener.onClickFunction(getSobotActivity(), SobotFunctionType.ZC_PhoneCustomerService);
                }
                ChatUtils.callUp(SobotUIConfig.sobot_title_right_menu3_call_num, getContext());
            } else {
                LogUtils.e("????????????????????????");
            }
        }
    }

    //????????????
    private void showAudioRecorder() {
        try {
            mFileName = SobotPathManager.getInstance().getVoiceDir() + "sobot_tmp.wav";
            String state = android.os.Environment.getExternalStorageState();
            if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
                LogUtils.i("SD Card is not mounted,It is  " + state + ".");
            }
            File directory = new File(mFileName).getParentFile();
            if (!directory.exists() && !directory.mkdirs()) {
                LogUtils.i("Path to file could not be created");
            }
            extAudioRecorder = ExtAudioRecorder.getInstanse(false);
            extAudioRecorder.setOutputFile(mFileName);
            extAudioRecorder.prepare();
            extAudioRecorder.start(new ExtAudioRecorder.AudioRecorderListener() {
                @Override
                public void onHasPermission() {
                    hidePanelAndKeyboard(mPanelRoot);
                    editModelToVoice(View.VISIBLE, "");// ??????????????????
                    if (btn_press_to_speak.getVisibility() == View.VISIBLE) {
                        btn_press_to_speak.setVisibility(View.VISIBLE);
                        btn_press_to_speak.setClickable(true);
                        btn_press_to_speak.setOnTouchListener(new PressToSpeakListen());
                        btn_press_to_speak.setEnabled(true);
                        txt_speak_content.setText(getResString("sobot_press_say"));
                        txt_speak_content.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNoPermission() {
                    ToastUtil.showToast(mAppContext, getResString("sobot_no_record_audio_permission"));
                }
            });
            stopVoice();
        } catch (Exception e) {
            LogUtils.i("prepare() failed");
        }
    }

    private void showRobotVoiceHint() {
        send_voice_robot_hint.setVisibility(current_client_model == ZhiChiConstant.client_model_robot ? View.VISIBLE : View.GONE);
    }

    private void hideRobotVoiceHint() {
        send_voice_robot_hint.setVisibility(View.GONE);
    }

    /**
     * ?????????????????????
     *
     * @param content
     */
    @Override
    protected void sendMsg(String content) {
        if (initModel == null) {
            return;
        }

        String msgId = System.currentTimeMillis() + "";

        if (ZhiChiConstant.client_model_robot == current_client_model) {
            if (type == 4 && initModel.getInvalidSessionFlag() == 1 && customerState != CustomerState.Queuing && TextUtils.isEmpty(tempMsgContent)) {
                //????????????,???????????????????????????????????????????????????????????????,????????????????????????
                //?????????????????????????????????????????????????????????
                tempMsgContent = content;
                doClickTransferBtn();
                return;
            }
            if (type == 2) {
                if (initModel.getInvalidSessionFlag() == 1) {
                    //???????????????????????????????????????,????????????????????????
                    tempMsgContent = content;
                }
                doClickTransferBtn();
                return;
            } else if ((type == 3 || type == 4) && info.getTransferKeyWord() != null) {
                //??????????????????????????? ???????????????
                HashSet<String> transferKeyWord = info.getTransferKeyWord();
                if (!TextUtils.isEmpty(content) && transferKeyWord.contains(content)) {
                    sendTextMessageToHandler(msgId, content, handler, 1, SEND_TEXT);
                    doClickTransferBtn();
                    return;
                }
            }
        }

        // ??????Handler?????? ????????????ui
        sendTextMessageToHandler(msgId, content, handler, 2, SEND_TEXT);

        LogUtils.i("???????????????????????????" + current_client_model);
        setTimeTaskMethod(handler);
        sendMessageWithLogic(msgId, content, initModel, handler, current_client_model, 0, "");
    }

    /**
     * ??????????????????
     *
     * @param consultingContent
     */
    protected void sendCardMsg(ConsultingContent consultingContent) {
        if (initModel == null || consultingContent == null) {
            return;
        }
        final String title = consultingContent.getSobotGoodsTitle();
        final String fromUrl = consultingContent.getSobotGoodsFromUrl();
        if (customerState == CustomerState.Online
                && current_client_model == ZhiChiConstant.client_model_customService
                && !TextUtils.isEmpty(fromUrl) && !TextUtils.isEmpty(title)) {
            String msgId = System.currentTimeMillis() + "";

            setTimeTaskMethod(handler);
            sendHttpCardMsg(initModel.getPartnerid(), initModel.getCid(), handler, msgId, consultingContent);
        }
    }

    /**
     * ????????????????????????
     *
     * @param orderCardContent
     */
    protected void sendOrderCardMsg(OrderCardContentModel orderCardContent) {
        if (initModel == null || orderCardContent == null) {
            return;
        }
        final String title = orderCardContent.getOrderCode();
        if (customerState == CustomerState.Online
                && current_client_model == ZhiChiConstant.client_model_customService
                && !TextUtils.isEmpty(title)) {
            String msgId = System.currentTimeMillis() + "";
            setTimeTaskMethod(handler);
            sendHttpOrderCardMsg(initModel.getPartnerid(), initModel.getCid(), handler, msgId, orderCardContent);
        }
    }

    /**
     * ???????????????
     * ??????????????????????????? ????????? ????????????????????? ????????? ?????????????????????????????????
     *
     * @param isActive    ?????????????????????  true ??????  flase ??????
     * @param score       ?????????
     * @param isSolve     ??????????????? 0 ????????????  1 ?????????
     * @param checklables ???????????????????????????
     */
    public void submitEvaluation(boolean isActive, int score, int isSolve, String checklables) {
        if (isComment) {
            hidePanelAndKeyboard(mPanelRoot);
            showHint(getResString("sobot_completed_the_evaluation"));
        } else {
            if (isUserBlack()) {
                showHint(getResString("sobot_unable_to_evaluate"));
            } else if (isAboveZero) {
                if (isActive()) {
                    if (mEvaluateDialog == null || !mEvaluateDialog.isShowing()) {
                        mEvaluateDialog = ChatUtils.showEvaluateDialog(getSobotActivity(), isSessionOver, false, false, initModel, current_client_model, isActive ? 1 : 0, currentUserName, score, isSolve, checklables, false, false);
                    }
                }
            } else {
                showHint(getResString("sobot_after_consultation_to_evaluate_custome_service"));
            }
        }
    }

    public void showVoiceBtn() {
        if (current_client_model == ZhiChiConstant.client_model_robot && type != 2) {
            btn_model_voice.setVisibility(info.isUseVoice() && info.isUseRobotVoice() ? View.VISIBLE : View.GONE);
            view_model_split.setVisibility(info.isUseVoice() && info.isUseRobotVoice() ? View.VISIBLE : View.GONE);
            if(type == 1){
                //????????????????????????????????????
                view_model_split.setVisibility(View.GONE);
            }
        } else {
            view_model_split.setVisibility(View.GONE);
            btn_model_voice.setVisibility(info.isUseVoice() ? View.VISIBLE : View.GONE);
        }
    }

    private void sendMsgToRobot(ZhiChiMessageBase base, int sendType, int questionFlag, String docId) {
        sendMsgToRobot(base, sendType, questionFlag, docId, null);
    }

    private void sendMsgToRobot(ZhiChiMessageBase base, int sendType, int questionFlag, String docId, String multiRoundMsg) {
        if (!TextUtils.isEmpty(multiRoundMsg)) {
            sendTextMessageToHandler(base.getId(), multiRoundMsg, handler, 2, sendType);
        } else {
            sendTextMessageToHandler(base.getId(), base.getContent(), handler, 2, sendType);
        }
        ZhiChiReplyAnswer answer = new ZhiChiReplyAnswer();
        answer.setMsgType(ZhiChiConstant.message_type_text + "");
        answer.setMsg(base.getContent());
        base.setAnswer(answer);
        base.setSenderType(ZhiChiConstant.message_sender_type_customer + "");
        sendMessageWithLogic(base.getId(), base.getContent(), initModel, handler, current_client_model, questionFlag, docId);
    }

    /**
     * ?????? ?????????????????????
     */
    private void restMultiMsg() {
        for (int i = 0; i < messageList.size(); i++) {
            ZhiChiMessageBase data = messageList.get(i);
            if (data.getAnswer() != null && data.getAnswer().getMultiDiaRespInfo() != null
                    && !data.getAnswer().getMultiDiaRespInfo().getEndFlag()) {
                data.setMultiDiaRespEnd(1);
            }
        }
        messageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            LogUtils.i("???????????????????????????" + requestCode + "--" + resultCode + "--" + data);

            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == ZhiChiConstant.REQUEST_CODE_picture) { // ??????????????????
                    if (data != null && data.getData() != null) {
                        Uri selectedImage = data.getData();
                        if (selectedImage == null) {
                            selectedImage = ImageUtils.getUri(data, getSobotActivity());
                        }
                        String path = ImageUtils.getPath(getSobotActivity(), selectedImage);
                        if (MediaFileUtils.isVideoFileType(path)) {
                            MediaPlayer mp = new MediaPlayer();
                            try {
                                mp.setDataSource(getSobotActivity(), selectedImage);
                                mp.prepare();
                                int videoTime = mp.getDuration();
                                if (videoTime / 1000 > 15) {
                                    ToastUtil.showToast(getSobotActivity(), getResString("sobot_upload_vodie_length"));
                                    return;
                                }
                                //SobotDialogUtils.startProgressDialog(getSobotActivity());
                                File videoFile = new File(path);
                                if (videoFile.exists()) {
                                    uploadVideo(videoFile, selectedImage, messageAdapter);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            ChatUtils.sendPicByUri(mAppContext, handler, selectedImage, initModel, lv_message, messageAdapter, false);
                        }
                    } else {
                        ToastUtil.showLongToast(mAppContext, getResString("sobot_did_not_get_picture_path"));
                    }

                }
                hidePanelAndKeyboard(mPanelRoot);
            }
            if (data != null) {
                switch (requestCode) {
                    case ZhiChiConstant.REQUEST_COCE_TO_GRROUP:
                        boolean toLeaveMsg = data.getBooleanExtra("toLeaveMsg", false);
                        int groupIndex = data.getIntExtra("groupIndex", -1);
                        if (toLeaveMsg) {
                            SharedPreferencesUtil.saveStringData(getSobotActivity(), ZhiChiConstant.sobot_connect_group_id, list_group != null ? list_group.get(groupIndex).getGroupId() : "");
                            startToPostMsgActivty(false);
                            return;
                        }
                        int tmpTransferType = data.getIntExtra("transferType", 0);
                        LogUtils.i("groupIndex-->" + groupIndex);
                        if (groupIndex >= 0) {
                            String docId = data.getStringExtra(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_DOCID);
                            String unknownQuestion = data.getStringExtra(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_UNKNOWNQUESTION);
                            String activeTransfer = data.getStringExtra(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_ACTIVETRANSFER);
                            SobotConnCusParam param = new SobotConnCusParam();
                            param.setGroupId(list_group.get(groupIndex).getGroupId());
                            param.setGroupName(list_group.get(groupIndex).getGroupName());
                            param.setTransferType(tmpTransferType);
                            param.setDocId(docId);
                            param.setUnknownQuestion(unknownQuestion);
                            param.setActiveTransfer(activeTransfer);
                            requestQueryFrom(param, info.isCloseInquiryForm());
                        }
                        break;
                    case ZhiChiConstant.REQUEST_COCE_TO_QUERY_FROM:
                        //??????????????????????????????
                        if (resultCode == ZhiChiConstant.REQUEST_COCE_TO_QUERY_FROM) {
                            String groupId = data.getStringExtra(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_GROUPID);
                            String groupName = data.getStringExtra(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_GROUPNAME);
                            int transferType = data.getIntExtra(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_TRANSFER_TYPE, 0);
                            String docId = data.getStringExtra(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_DOCID);
                            String unknownQuestion = data.getStringExtra(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_UNKNOWNQUESTION);
                            String activeTransfer = data.getStringExtra(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_ACTIVETRANSFER);
                            SobotConnCusParam param = new SobotConnCusParam();
                            param.setGroupId(groupId);
                            param.setGroupName(groupName);
                            param.setTransferType(transferType);
                            param.setDocId(docId);
                            param.setUnknownQuestion(unknownQuestion);
                            param.setActiveTransfer(activeTransfer);
                            connectCustomerService(param);
                        } else {
                            //??????????????????
                            isHasRequestQueryFrom = false;
                            if (type == ZhiChiConstant.type_custom_only) {
                                //???????????????????????????
                                isSessionOver = true;
                                //??????????????????
                                clearCache();
                                finish();
                            }
                        }
                        break;
                    case ZhiChiConstant.REQUEST_COCE_TO_CHOOSE_FILE:
                        Uri selectedFileUri = data.getData();
                        if (null == selectedFileUri) {
                            File selectedFile = (File) data.getSerializableExtra(ZhiChiConstant.SOBOT_INTENT_DATA_SELECTED_FILE);
                            uploadFile(selectedFile, handler, lv_message, messageAdapter, false);
                        } else {
                            String tmpMsgId = String.valueOf(System.currentTimeMillis());
                            if (selectedFileUri == null) {
                                selectedFileUri = ImageUtils.getUri(data, getSobotActivity());
                            }
                            String path = ImageUtils.getPath(getSobotActivity(), selectedFileUri);
                            if (TextUtils.isEmpty(path)) {
                                ToastUtil.showToast(getSobotActivity(), ResourceUtils.getResString(getSobotActivity(), "sobot_cannot_open_file"));
                                return;
                            }
                            File selectedFile = new File(path);
                            LogUtils.i("tmpMsgId:" + tmpMsgId);
                            uploadFile(selectedFile, handler, lv_message, messageAdapter, true);
                        }
                        break;
                    case REQUEST_CODE_CAMERA:
                        int actionType = SobotCameraActivity.getActionType(data);
                        if (actionType == SobotCameraActivity.ACTION_TYPE_VIDEO) {
                            File videoFile = new File(SobotCameraActivity.getSelectedVideo(data));
                            if (videoFile.exists()) {
                                String snapshotPath = SobotCameraActivity.getSelectedImage(data);
                                uploadVideo(videoFile, null, messageAdapter);
                            } else {
                                ToastUtil.showLongToast(mAppContext, getResString("sobot_pic_select_again"));
                            }
                        } else {
                            File tmpPic = new File(SobotCameraActivity.getSelectedImage(data));
                            if (tmpPic.exists()) {
                                ChatUtils.sendPicLimitBySize(tmpPic.getAbsolutePath(), initModel.getCid(),
                                        initModel.getPartnerid(), handler, mAppContext, lv_message, messageAdapter, true);
                            } else {
                                ToastUtil.showLongToast(mAppContext, getResString("sobot_pic_select_again"));
                            }
                        }
                        break;
                    case SobotPostLeaveMsgActivity.EXTRA_MSG_LEAVE_REQUEST_CODE:
                        //????????????
                        String content = SobotPostLeaveMsgActivity.getResultContent(data);
                        ZhiChiMessageBase tmpMsg = ChatUtils.getLeaveMsgTip(content);

                        ZhiChiMessageBase tmpMsg2 = ChatUtils.getTipByText(ResourceUtils.getResString(getContext(), "sobot_leavemsg_success_tip"));
                        messageAdapter.justAddData(tmpMsg);
                        messageAdapter.justAddData(tmpMsg2);
                        messageAdapter.notifyDataSetChanged();
                        customerServiceOffline(initModel, 99);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class PressToSpeakListen implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            isCutVoice = false;
            // ?????????????????????????????????
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    voiceMsgId = System.currentTimeMillis() + "";
                    // ????????????????????????
                    btn_upload_view.setClickable(false);
                    btn_model_edit.setClickable(false);
                    btn_upload_view.setEnabled(false);
                    btn_model_edit.setEnabled(false);
                    if (Build.VERSION.SDK_INT >= 11) {
                        btn_upload_view.setAlpha(0.4f);
                        btn_model_edit.setAlpha(0.4f);
                    }
                    stopVoiceTimeTask();
                    v.setPressed(true);
                    voice_time_long.setText("00" + "''");
                    voiceTimeLongStr = "00:00";
                    voiceTimerLong = 0;
                    currentVoiceLong = 0;
                    recording_container.setVisibility(View.VISIBLE);
                    voice_top_image.setVisibility(View.VISIBLE);
                    mic_image.setVisibility(View.VISIBLE);
                    mic_image_animate.setVisibility(View.VISIBLE);
                    voice_time_long.setVisibility(View.VISIBLE);
                    recording_timeshort.setVisibility(View.GONE);
                    image_endVoice.setVisibility(View.GONE);
                    txt_speak_content.setText(getResString("sobot_up_send"));
                    // ???????????????????????????
                    startVoice();
                    return true;
                // ?????????????????????
                case MotionEvent.ACTION_POINTER_DOWN:
                    return true;
                case MotionEvent.ACTION_POINTER_UP:
                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (!is_startCustomTimerTask) {
                        noReplyTimeUserInfo = 0;
                    }

                    if (event.getY() < 10) {
                        // ?????????????????????
                        voice_top_image.setVisibility(View.GONE);
                        image_endVoice.setVisibility(View.VISIBLE);
                        mic_image.setVisibility(View.GONE);
                        mic_image_animate.setVisibility(View.GONE);
                        recording_timeshort.setVisibility(View.GONE);
                        txt_speak_content.setText(getResString("sobot_release_to_cancel"));
                        recording_hint.setText(getResString("sobot_release_to_cancel"));
                        recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg"));
                    } else {
                        if (voiceTimerLong != 0) {
                            txt_speak_content.setText(getResString("sobot_up_send"));
                            voice_top_image.setVisibility(View.VISIBLE);
                            mic_image_animate.setVisibility(View.VISIBLE);
                            image_endVoice.setVisibility(View.GONE);
                            mic_image.setVisibility(View.VISIBLE);
                            recording_timeshort.setVisibility(View.GONE);
                            recording_hint.setText(getResString("sobot_move_up_to_cancel"));
                            recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg1"));
                        }
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    // ?????????????????????
                    int toLongOrShort = 0;
                    btn_upload_view.setClickable(true);
                    btn_model_edit.setClickable(true);
                    btn_upload_view.setEnabled(true);
                    btn_model_edit.setEnabled(true);
                    if (Build.VERSION.SDK_INT >= 11) {
                        btn_upload_view.setAlpha(1f);
                        btn_model_edit.setAlpha(1f);
                    }
                    v.setPressed(false);
                    txt_speak_content.setText(getResString("sobot_press_say"));
                    stopVoiceTimeTask();
                    stopVoice();
                    if (recording_container.getVisibility() == View.VISIBLE
                            && !isCutVoice) {
                        hidePanelAndKeyboard(mPanelRoot);
                        if (animationDrawable != null) {
                            animationDrawable.stop();
                        }
                        voice_time_long.setText("00" + "''");
                        voice_time_long.setVisibility(View.INVISIBLE);
                        if (event.getY() < 0) {
                            recording_container.setVisibility(View.GONE);
                            sendVoiceMap(2, voiceMsgId);
                            return true;
                            // ??????????????????
                        } else {
                            // ????????????
                            if (currentVoiceLong < 1 * 1000) {
                                voice_top_image.setVisibility(View.VISIBLE);
                                recording_hint.setText(getResString("sobot_voice_time_short"));
                                recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg"));
                                recording_timeshort.setVisibility(View.VISIBLE);
                                voice_time_long.setVisibility(View.VISIBLE);
                                voice_time_long.setText("00:00");
                                mic_image.setVisibility(View.GONE);
                                mic_image_animate.setVisibility(View.GONE);
                                toLongOrShort = 0;
                                sendVoiceMap(2, voiceMsgId);
                            } else if (currentVoiceLong < minRecordTime * 1000) {
                                recording_container.setVisibility(View.GONE);
                                sendVoiceMap(1, voiceMsgId);
                                return true;
                            } else if (currentVoiceLong > minRecordTime * 1000) {
                                toLongOrShort = 1;
                                voice_top_image.setVisibility(View.VISIBLE);
                                recording_hint.setText(getResString("sobot_voiceTooLong"));
                                recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg"));
                                recording_timeshort.setVisibility(View.VISIBLE);
                                mic_image.setVisibility(View.GONE);
                                mic_image_animate.setVisibility(View.GONE);
                            } else {
                                sendVoiceMap(2, voiceMsgId);
                            }
                        }
                        currentVoiceLong = 0;
                        closeVoiceWindows(toLongOrShort);
                    } else {
                        sendVoiceMap(2, voiceMsgId);
                    }
                    voiceTimerLong = 0;
                    restartMyTimeTask(handler);
                    // mFileName
                    return true;
                default:
                    sendVoiceMap(2, voiceMsgId);
                    closeVoiceWindows(2);
                    return true;
            }
        }
    }

    // ??????????????????
    public String getActivityTitle() {
        return mTitleTextView.getText().toString();
    }

    /**
     * ???????????????
     *
     * @return true ????????????
     */
    public void onBackPress() {
        if (isActive()) {
            //???????????????????????? ?????????????????????????????????  ???????????????????????????????????????????????????
            if (mPanelRoot.getVisibility() == View.VISIBLE) {
                hidePanelAndKeyboard(mPanelRoot);
                return;
            } else {
                if (info.isShowSatisfaction()) {
                    if (isAboveZero && !isComment) {
                        // ????????? ??????????????????????????? ?????? ????????????
                        mEvaluateDialog = ChatUtils.showEvaluateDialog(getSobotActivity(), isSessionOver, true, false, initModel,
                                current_client_model, 1, currentUserName, 5, 0, "", true, true);
                        return;
                    }
                }
            }
            finish();
        }
    }

    protected String getSendMessageStr() {
        return et_sendmessage.getText().toString().trim();
    }

    private void sobotCustomMenu() {
        if (!initModel.isLableLinkFlag()) {
            return;
        }
        final int marginRight = (int) getDimens("sobot_layout_lable_margin_right");
        //???????????????????????????
        zhiChiApi.getLableInfoList(SobotChatFSFragment.this, initModel.getPartnerid(), new StringResultCallBack<List<SobotLableInfoList>>() {
            @Override
            public void onSuccess(final List<SobotLableInfoList> infoLists) {
                if (!isActive()) {
                    return;
                }

                sobot_custom_menu_linearlayout.removeAllViews();
                if (infoLists != null && infoLists.size() > 0) {
                    for (int i = 0; i < infoLists.size(); i++) {
                        final TextView tv = (TextView) View.inflate(getContext(), getResLayoutId("sobot_layout_lable"), null);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0, 0, marginRight, 0);
                        tv.setLayoutParams(layoutParams);
                        tv.setText(infoLists.get(i).getLableName());
                        tv.setTag(infoLists.get(i).getLableLink());
                        sobot_custom_menu_linearlayout.addView(tv);
                        if (!TextUtils.isEmpty(tv.getTag() + "")) {
                            tv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    hidePanelAndKeyboard(mPanelRoot);
                                    if (SobotOption.hyperlinkListener != null) {
                                        SobotOption.hyperlinkListener.onUrlClick(v.getTag() + "");
                                        return;
                                    }
                                    if (SobotOption.newHyperlinkListener != null) {
                                        //????????????true,??????;false ?????????
                                        boolean isIntercept = SobotOption.newHyperlinkListener.onUrlClick(getSobotActivity(), v.getTag() + "");
                                        if (isIntercept) {
                                            return;
                                        }
                                    }

                                    Intent intent = new Intent(getContext(), WebViewActivity.class);
                                    intent.putExtra("url", v.getTag() + "");
                                    getSobotActivity().startActivity(intent);
                                }
                            });
                        }
                    }
                    sobot_custom_menu.setVisibility(View.VISIBLE);
                } else {
                    sobot_custom_menu.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                if (!isActive()) {
                    return;
                }
                sobot_custom_menu.setVisibility(View.GONE);
            }
        });
    }

    /**
     * ????????????????????????????????????
     */
    private void showSwitchRobotBtn() {
        if (initModel != null && type != 2 && current_client_model == ZhiChiConstant.client_model_robot) {
            sobot_ll_switch_robot.setVisibility(initModel.isRobotSwitchFlag() ? View.VISIBLE : View.GONE);
        } else {
            sobot_ll_switch_robot.setVisibility(View.GONE);
        }
    }

    /**
     * ??????????????????????????????
     */
    @Override
    public void onSobotRobotListItemClick(SobotRobot sobotRobot) {
        if (initModel != null && sobotRobot != null) {
            initModel.setGuideFlag(sobotRobot.getGuideFlag());
            initModel.setRobotid(sobotRobot.getRobotFlag());
            initModel.setRobotLogo(sobotRobot.getRobotLogo());
            initModel.setRobotName(sobotRobot.getRobotName());
            initModel.setRobotHelloWord(sobotRobot.getRobotHelloWord());
            showLogicTitle(initModel.getRobotName(), initModel.getRobotLogo(), false);
            List<ZhiChiMessageBase> datas = messageAdapter.getDatas();
            int count = 0;
            for (int i = datas.size() - 1; i >= 0; i--) {
                if ((ZhiChiConstant.message_sender_type_robot_welcome_msg + "").equals(datas.get(i).getSenderType())
                        || (ZhiChiConstant.message_sender_type_questionRecommend + "").equals(datas.get(i).getSenderType())
                        || (ZhiChiConstant.message_sender_type_robot_guide + "").equals(datas.get(i).getSenderType())) {
                    datas.remove(i);
                    count++;
                    if (count >= 3) {
                        break;
                    }
                }
            }
            messageAdapter.notifyDataSetChanged();
            //????????????????????????UI
            remindRobotMessageTimes = 0;
            remindRobotMessage(handler, initModel, info);
        }
    }

    private void applyUIConfig() {
        if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_serviceImgId) {
            btn_set_mode_rengong.setBackgroundResource(SobotUIConfig.sobot_serviceImgId);
        }

        if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_chat_bottom_bgColor) {
            sobot_ll_bottom.setBackgroundColor(getContext().getResources().getColor(SobotUIConfig.sobot_chat_bottom_bgColor));
        }

        if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_apicloud_titleBgColor) {
            relative.setBackgroundColor(SobotUIConfig.sobot_apicloud_titleBgColor);
        }

        if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_titleBgColor) {
            relative.setBackgroundColor(getContext().getResources().getColor(SobotUIConfig.sobot_titleBgColor));
        }

        if (SobotUIConfig.sobot_title_right_menu2_display) {
            sobot_tv_right_second.setVisibility(View.VISIBLE);
            if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_title_right_menu2_bg) {
                Drawable img = getResources().getDrawable(SobotUIConfig.sobot_title_right_menu2_bg);
                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                sobot_tv_right_second.setCompoundDrawables(null, null, img, null);
            }

        }

        if (SobotUIConfig.sobot_title_right_menu3_display) {
            sobot_tv_right_third.setVisibility(View.VISIBLE);
            if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_title_right_menu3_bg) {
                Drawable img = getResources().getDrawable(SobotUIConfig.sobot_title_right_menu3_bg);
                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                sobot_tv_right_third.setCompoundDrawables(null, null, img, null);
            }

        }
    }
}
