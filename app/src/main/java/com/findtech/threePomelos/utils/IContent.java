package com.findtech.threePomelos.utils;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.mydevices.bean.DeviceCarBean;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.mydevices.bean.BluetoothLinkBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex on 2017/5/3.
 * <pre>
 *     author  ： Alex
 *     e-mail  ： 18238818283@sina.cn
 *     time    ： 2017/05/03
 *     desc    ：
 *     version ： 1.0
 */
public class IContent {

    private static   IContent iContent = new IContent();
    private IContent() {
    }

    public static IContent getInstacne(){
        return iContent;
    }

    public static  final int[] tabImageIds = new int[] { R.drawable.tab_home,R.drawable.tab_music,R.drawable.tab_health,R.drawable.tab_user};
    public static  final int[] tabtextIds = new int[] { R.string.page_tab1,R.string.page_tab2,R.string.page_tab3,R.string.page_tab4};
    public static final String FILEM_USIC = "Music";
    public static final String UPDATE = "Update";
    public static final String IS_FIRST_USE = "isFirstUse";
    public static final String IS_VERSION = "is_version";
    public static final String VERSION_UPDATE = "version_update";
    public static final String APP_UPDATE = "app_update";
   public static final String MEI_ZU = "Meizu";

    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String NUMBER = "number";
    public static final int[] MUSIC_SEC_IMAGE = new int[]{R.drawable.face_image_rhyme,R.drawable.face_image_poetry,R.drawable.face_child_story,R.drawable.face_english,R.drawable.face_image_three_character,R.drawable.face_music_car};
    public static final int[] MUSIC_BACKCOLOR = new int[]{R.color.music_divider_rhyme,R.color.music_divider_poetry,R.color.music_divider_story,R.color.music_divider_english,R.color.music_divider_three,R.color.music_divider_car};
    public static final int[] MUSIC_SEC_IMAGE_O = new int[]{R.drawable.face_image_rhyme_a,R.drawable.face_image_poetry_a,R.drawable.face_child_story_a,R.drawable.face_english_a,R.drawable.face_image_three_character_a,R.drawable.face_music_car_a};
    public static final String SINGLE_CLICK = "com.findtech.threePomelos.playdetail";
    public static final String DOUBLE_CLICK = "com.findtech.threePomelos.stopplay";

    /** ---------------------------------------------  BlueTooth------------------------------------------------*/
    public static final String SERVERUUID_PRE = "ffe0";
    public static final String CHARACTERUUID_PRE = "ffe4";
    public static final String SERVERUUID_BLE = "a032";
    public static final String CHARACTERUUID_BLE = "a042";
    public static final String WRITEUUID_BLE = "a040";
    public static final String READUUID_BLE = "a041";

    public static boolean  isBLE = true;
    public static byte SDMODE[] = {0x55, (byte) 0xAA,0x01,0x01,0x02, 0x01,(0-(4+0x01))};
    public static byte BLUEMODE[] = {0x55, (byte) 0xAA,0x01,0x01,0x02, 0x03,(0-(4+0x03))};
    public static byte READMODE[] = {0x55, (byte) 0xAA,0x00,0x01,0x03, (byte) 0x0FC};
    public static byte GET_FILE[] =   {0x55, (byte) 0xaa,0x04,0x02,0x03,0x00,0x00,0x00,0x01,(0 - 0x0a)};
    public static byte NOTIFY_DATA [] = {0x55, (byte) 0xAA, 0x00, 0x0B, 0x0c, (byte) 0xE9};
    public static byte CLOSE_DEVICE [] = {0x55, (byte) 0xAA, 0x00, 0x0B, (byte) 0xAA, (byte) 0x4B};
    public static byte BRAKE_CAR [] = {0x55, (byte) 0xAA, 0x00, 0x0B, (byte) 0x05, (byte) 0xF0};
    public static byte BRAKE_CAR_CLEAR [] = {0x55, (byte) 0xAA, 0x00, 0x0B, (byte) 0x06, (byte) 0xEF};
    public static byte REPAIR_CAR [] = {0x55, (byte) 0xAA, 0x00, 0x0B, 0x55, (byte) 0xA0};

    /**
     * Combi bluetooth control
     */

    public static final byte COMBI_NOTIFY[] =  {0x55, (byte) 0xAA, 0x00, 0x0C, 0x0C, (byte) 0xE8};


    public byte [] WRITEVALUE;
    public String address = null;
    public String deviceName = null;
    public String functionType = null;
    public String company = null;

    public Map<String,MusicInfo> map = new HashMap<>();
    public List<String> collectedList =  new ArrayList<>();
    public ArrayList<BluetoothLinkBean> bluetoothLinkBeen = new ArrayList<>();
    public String clickPositionAddress = "clickPositionAddress";
    public String clickPositionName = "clickPositionName";
    public String clickPositionFunction = "clickPositionFunction";
    public String newCode = null;
    public String newCodeUrl = null;
    public String newVersion = null;
    public  String  clickPositionType = null;
    public boolean isBind = false;
    public String code = "V1.0.1";
    public boolean SD_Mode = false;
    public static boolean isSended = false;
    public ArrayList<DeviceCarBean> addressList = new ArrayList<>();
    public static boolean isModePlay = true;
    public static  final String ACTION_PLAY_OR_PAUSE = "ACTION_PLAY_OR_PAUSE";
    public static String MUSIC_NAME = null;

}
