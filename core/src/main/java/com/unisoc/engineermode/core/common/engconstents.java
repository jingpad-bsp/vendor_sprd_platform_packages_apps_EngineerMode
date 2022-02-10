package com.unisoc.engineermode.core.common;

public interface engconstents {
    int ENG_AT_SETARMLOG = 4; // start/stop armlog
    int ENG_AT_SETCAPLOG = 58;
    String ENG_AT_REQUEST_MODEM_VERSION = "AT+CGMM"; // get version
    String ENG_AT_CGMR = "AT+CGMR";
    String ENG_AT_DSPVERSION = "AT+SPDSPVERSION=";
    String ENG_AT_CCED = "AT+CCED="; // net info of sim
    String ENG_AT_SGMR = "AT+SGMR=";
    String ENG_AT_SET_MANUAL_ASSERT = "AT+SPATASSERT=1";
    String ENG_AT_SFPL = "AT+SFPL";
    String ENG_AT_SEPL = "AT+SEPL";
    String ENG_AT_COPS = "AT+COPS?";
    String ENG_AT_CURRENT_GSMBAND = "AT+SBAND=0,13";
    String ENG_AT_SELECT_GSMBAND = "AT+SBAND=1,13,";
    String ENG_AT_TD_LOCKED_BAND = "AT+SPLOCKBAND?";
    String ENG_AT_TD_SET_BAND = "AT+SPLOCKBAND=";
    String ENG_AT_W_LOCKED_BAND = "AT+SPFDDBAND=0,";
    String ENG_AT_W_LOCK_BAND = "AT+SPFDDBAND=1,";
    String ENG_AT_W_SET_BAND = "AT+SPFDDBAND=";
    String ENG_AT_TD_CAPABILITY = "AT+SPCAPABILITY=3,0";
    String ENG_AT_W_CAPABILITY = "AT+SPCAPABILITY=4,0";
    String ENG_AT_GSM_CAPABILITY = "AT+SPCAPABILITY=2,0";
    String ENG_AT_SPAUTOPOWERON = "AT+SPAUTOPOWER=";
    String ENG_AT_GETSPAUTOPOWERON = "AT+SPAUTOPOWER?";
    String ENG_AT_CURRENT_BAND = "AT+SBAND?";
    String ENG_AT_SELECT_BAND = "AT+SBAND=";
    String ENG_AT_GETARMLOG1 = "AT+ARMLOG?";
    String ENG_AT_GETDSPLOG1 = "AT+SPDSPOP?";
    String ENG_AT_GETAUDIOLOG1 = "AT+SPDSPOP?";
    String ENG_AT_GETCAPLOG1 = "AT+SPCAPLOG?";
    String ENG_AT_SETARMLOG1 = "AT+ARMLOG=";
    String ENG_AT_SETCAPLOG1 = "AT+SPCAPLOG=";
    String ENG_AT_SETDSPLOG1 = "AT+SPDSPOP=";
    String ENG_AT_SETIQLOGOPEN = "AT+SPDSP=1,1,0,0";
    String ENG_AT_SETIQLOGClose = "AT+SPDSP=1,0,0,0";
    String ENG_AT_GETAUTOATT = "AT+SAUTOATT?";
    String ENG_AT_SETAUTOATT = "AT+SAUTOATT=";
    String ENG_AT_SETPDPACTIVE1 = "AT+CGACT=";
    String ENG_AT_GETPDPACTIVE1 = "AT+CGACT?";
    String ENG_AT_GETSMSSERVER = "AT+CGSMS?";
    String ENG_AT_SETSMSSERVER = "AT+CGSMS=";
    String ENG_AT_SPFRQ = "AT+SPFRQ=";
    String ENG_AT_SPFRQ1 = "AT+SPFRQ?";
    String ENG_AT_SPGSMFRQ = "AT+SPGSMFRQ=";
    String ENG_AT_SPGSMFRQ1 = "AT+SPGSMFRQ?";
    String ENG_AT_CAOCQ1 = "AT+SPTEST?";
    String ENG_AT_CAOCSET1 = "AT+SPTEST=3,";
    String ENG_AT_CAMM1 = "AT+CAMM=";
    String ENG_AT_SGPRSDATA1 = "AT+SGPRSDATA=";
    String ENG_AT_SPENGMD_QUERY1 = "AT+SPENGMD=0,10,2";
    String ENG_AT_SPENGMD_OPEN1 = "AT+SPENGMD=1,10,2,3";
    String ENG_AT_SPENGMD_CLOSE1 = "AT+SPENGMD=1,10,2,1";
    String ENG_AT_GETTIMECONFLICT = "AT+SPCAPABILITY=1,0";
    String ENG_AT_SETTIMECONFLICT = "AT+SPCAPABILITY=1,1,";
    String ENG_AT_SETAUDIOLOG = "AT+SPDSP=65535,0,0,";
    String ENG_AT_NETINFO_STATI = "AT+SPENGMD=0,3,2";
    String ENG_AT_APNQUERY = "AT+CGDCONT?";
    String ENG_AT_GETHPLMN = "AT+CIMI";
    String ENG_AT_GETRPLMN = "AT+CREG?";
    String ENG_AT_SETCCFC = "AT+CCFC=";
    String ENG_AT_GETCCFC = "AT+CCFC?";
    String ENG_AT_SET_SPTEST = "AT+SPTEST=";
    String ENG_AT_SET_SPTEST1 = "AT+SPTEST?";
    String ENG_AT_GPRS = "AT+CGATT=";
    String ENG_AT_GPRS1 = "AT+CGATT?";
    String ENG_AT_USIMDRIVERLOG = "AT+SPUSIMDRVLS=";
    String ENG_AT_USIMDRIVERLOG1 = "AT+SPUSIMDRVLS?";
    String ENG_AT_NETMODE = "AT^SYSCONFIG=";
    String ENG_AT_NETMODE1 = "AT^SYSCONFIG?";
    String ENG_AUTO_ANSWER = "AT+SPAUTO=";
    String ENG_GET_AUTO_ANSWER = "AT+SPAUTO?";
    String ENG_AT_SETSPFRQ1 = "AT+SPFRQ=";
    String ENG_AT_GETSPFRQ1 = "AT+SPFRQ?";
    String ENG_AT_SPGETFDY = "AT*FDY?";
    String ENG_AT_SPSETFDY = "AT*FDY=";
    String ENG_AT_CLCK = "AT+CLCK=";
    String ENG_AT_ADDSIM = "AT+SPSMNW=";
    String ENG_AT_GETIMSI = "AT+SPACTCARD=0";
    String ENG_AT_SETCB = "AT+CSCB=";
    String ENG_AT_GETCB = "AT+CSCB?";
    String ENG_DATA_SERVICES_PRE = "AT+SPBPM=";
    String ENG_RPLMN_USIM = "AT+CRSM=176,28539,0,0,12,\"3F007F20\"";
    String ENG_RPLMN_SIM = "AT+CRSM=176,28539,0,0,24,0,\"3F007FFF\"";
    String ENG_SEND_POWER = "AT+SPMAXRF=";
    String ENG_AT_CGDSCONT = "AT+CGDSCONT=";
    String ENG_AT_CGTFT = "AT+CGTFT=";
    String ENG_GET_SNVM = "AT+SNVM=0,";
    String ENG_SET_SNVM = "AT+SNVM=1,";
    String ENG_GET_WPREFER_SWITCH = "AT+SPWPREFERSWITCH?";
    String ENG_SET_WPREFER_SWITCH = "AT+SPWPREFERSWITCH=";
    String ENG_AT_LTEBGTIMER = "AT+LTEBGTIMER=";
    String ENG_AT_LTESETRSRP = "AT+LTESETRSRP=";
    String ENG_AT_RESET = "AT+RESET=1";
    String ENG_AT_IQMENU = "AT+SPIQMENU=";

    String ENG_AT_GET_LTE_DATA_IMPEDE = "AT+SPLTECFG?";
    String ENG_AT_SET_LTE_DATA_IMPEDE = "AT+SPLTECFG=";
    /*SPRD: modify 20140621 Spreadtrum of 325713 EngineerMode, telephony-LTE modem add Clear Prior Information@{ */
    String ENG_AT_CLEAR_PRIOR = "AT+SPCLEANINFO";
    /*Store meid when Sim card is initialized*/
    String ENG_AT_GET_MEID_SWITCH = "AT+SPCARDINFO=0,1,0";
    // open the switch of store meid to sim
    String ENG_AT_OPEN_MEID_SWITCH = "AT+SPCARDINFO=1,0,1";
    //close the switch of store meid to sim
    String ENG_AT_CLOSE_MEID_SWITCH = "AT+SPCARDINFO=1,0,0";

    /*@}*/
    String ENG_AT_GET_NXP = "AT+SPTEST=30,0";
    String ENG_AT_SET_NXP_STATUS = "AT+SPTEST=30,1";
    String ENG_AT_GET_MOS_STATUS = "AT+SPCAPABILITY=45,0";
    String ENG_AT_SET_MOS_STATUS = "AT+SPCAPABILITY=45,1";
    String ENG_AT_GET_LTE_BAND = "AT+SPLBAND=0";
    String ENG_AT_SET_LTE_BAND = "AT+SPLBAND=1,";
    String ENG_AT_GET_SIM_TRACE = "AT+SPUSIMDRVLS?";
    String ENG_AT_SET_SIM_TRACE = "AT+SPUSIMDRVLS=";
    String ENG_AT_CATEGARY = "AT+SPUECAT=0";
    String ENG_AT_GET_CSFB2GSM = "AT+SPSETGRRC?";
    String ENG_AT_SET_CSFB2GSM = "AT+SPSETGRRC=";
    String ENR_AT_SET_SPTEST = "AT+SPTEST=";
    String ENR_AT_SET_SPTRACELEVEL = "AT+SPTRACELEVEL=";
    String ENG_GET_AT_HSPA = "AT+SPHSPA=0";
    String ENG_SET_AT_HSPA = "AT+SPHSPA=1,";
    String ENG_SET_AT_SLEEP_LOG = "AT+SPSLEEPLOG";
    /* SPRD:these are for volte@{ */
    String ENG_VOLTE_IMPI = "AT+IMPI=";
    String ENG_VOLTE_IMPU = "AT+IMPU=";
    String ENG_VOLTE_DOMAIN = "AT+DOMAIN=";
    String ENG_VOLTE_IP_SETTING = "AT+SPENGMDVOLTE=4,";
    String ENG_VOLTE_MUT_SETTING = "AT+SPENGMDVOLTE=5,";
    String ENG_VOLTE_SUBSCRIB_SETTING = "AT+SPENGMDVOLTE=8,";
    String ENG_VOLTE_SIGCOMP_SETTING = "AT+SPENGMDVOLTE=9,";
    String ENG_EXPIERSTIMER_SETTING = "AT+SPENGMDVOLTE=11,";
    String ENG_VOICE_CODE_SETTING = "AT+SPENGMDVOLTE=12,";
    String ENG_PRECONDITION_SETTING = "AT+SPENGMDVOLTE=13,";
    String ENG_TQOS_TIMER_SETTING = "AT+SPENGMDVOLTE=14,";
    String ENG_TCALL_TIMER_SETTING = "AT+SPENGMDVOLTE=15,";
    String ENG_TREG_TIMER_SETTING = "AT+SPENGMDVOLTE=16,";
    String ENG_IPSEC_SETTING = "AT+SPENGMDVOLTE=10,";
    String ENG_PS_AUTH_TYPE_SETTING = "AT+APPSAUTHTYPE";
    /* @} */
    String ENG_AT_SPENGMD_007 = "AT+SPENGMD=0,0,7";
    String ENG_AT_SPENGMD_008 = "AT+SPENGMD=0,0,8";
    String ENG_AT_MEMORY_LEAK = "AT+SPENGMD=1,8,1,0";
    /* new slog */
    String ENG_SET_LOG_LEVEL = "AT+SPLOGLEVEL=";
    String ENG_GET_LOG_LEVEL = "AT+SPLOGLEVEL?";
    String ENG_SET_DSP_OPEN = "AT+SPDSP=65535,0,0,4096";
    String ENG_SET_DSP_CLOSE = "AT+SPDSP=65535,0,0,0";

    String ENG_CET_VAMOS_CPC = "AT+SPENGMD=";
    String ENG_AT_LTE_CATEGORY = "AT+SPUECAT=0";
    String ENG_AT_LTE_BAND = "AT+SPBANDCTRL=0,5";
    String ENG_AT_VOLTE_SUPPOERTED = "AT+CAVIMS?";

    // *SPRD: modify 20151026 add new at command
    String ENG_AT_SMS_GW_BEARER_PREF = "AT+CGSMS?";
    String ENG_AT_AGPS_DEFAULT_QOS_TIME = "AT+SPTEST=26";

    // begin 550429 add by suyan.yang 2016.05.10
    String ENG_GET_SMS_OVER_IP_SWITCH = "AT+CASIMS?";
    String ENG_SET_SMS_OVER_IP_SWITCH = "AT+CASIMS=";
    // end 550429 add by suyan.yang 2016.05.10

    String ENG_GET_SESSION_TIMER = "AT+SPENGMDVOLTE=23,0";
    String ENG_SET_SESSION_TIMER = "AT+SPENGMDVOLTE=23,1,";
    String ENG_GET_FORCE_MT_SESSION_TIMER = "AT+SPENGMDVOLTE=2,0";
    String ENG_SET_FORCE_MT_SESSION_TIMER = "AT+SPENGMDVOLTE=2,1,";
    /* BEGIN BUG540993 2016/05/11 sprd:EngineerMode add the parameter of CAP log packet length feature */
    String ENG_AT_GETCAPLOGLENGTH = "AT+SPCAPLEN?";
    String ENG_AT_SETCAPLOGLENGTH = "AT+SPCAPLEN=";
    /* END BUG540993 2016/05/11 sprd:EngineerMode add the parameter of CAP log packet length feature */
    String ENG_GET_ANTENNA_STATE = "AT+SPDUALRFSEL?";
    String ENG_SET_ANTENNA = "AT+SPDUALRFSEL=";

    String ENG_AT_SET_GCF = "AT+SPGCFIOTOPER=";
    String ENG_AT_GET_GCF = "AT+SPGCFIOTOPER?";
    String ENG_AT_GET_PQOS = "AT+SPENGMDVOLTE=25,0";
    String ENG_AT_SET_PQOS = "AT+SPENGMDVOLTE=25,1,";
    // ue_cap start
    String ENG_AT_SET_DIVERSITY = "AT+SPDIVERSITY=";
    String ENG_AT_GET_DIVERSITY = "AT+SPDIVERSITY?";
    String ENG_AT_SET_VAMOS = "at+spengmd = 1,0,7,0,";
    String ENG_AT_GET_VAMOS = "at+spengmd = 0,0,7";
    String ENG_AT_TEST = "AT";
    String ENG_AT_SET_ANR = ENG_AT_TEST;
    String ENG_AT_GET_ANR = ENG_AT_TEST;
    String ENG_AT_SET_DLCA = ENG_AT_TEST;
    String ENG_AT_GET_DLCA = ENG_AT_TEST;
    String ENG_AT_SET_ULCA = "AT+SPLTECFG=";
    String ENG_AT_GET_ULCA = ENG_AT_TEST;
    String ENG_AT_SET_TM9 = ENG_AT_TEST;
    String ENG_AT_GET_TM9 = ENG_AT_TEST;
    String ENG_AT_SET_EICIC = ENG_AT_TEST;
    String ENG_AT_GET_EICIC = ENG_AT_TEST;
    String ENG_AT_SET_EMBMS = ENG_AT_TEST;
    String ENG_AT_GET_EMBMS = ENG_AT_TEST;
    String ENG_AT_SET_MDT = ENG_AT_TEST;
    String ENG_AT_GET_MDT = ENG_AT_TEST;
    String ENG_AT_SET_MTA = ENG_AT_TEST;
    String ENG_AT_GET_MTA = ENG_AT_TEST;
    String ENG_AT_SET_EMFBI = ENG_AT_TEST;
    String ENG_AT_GET_EMFBI = ENG_AT_TEST;
    String ENG_AT_SET_FBI = ENG_AT_TEST;
    String ENG_AT_GET_FBI = ENG_AT_TEST;
    /* SPRD Bug 832621: Switch of UL 64QAM can not take effect. @{ */
    String ENG_AT_SET_UL_64QAM = "AT+spengmd=";
    /* @} */
    String ENG_AT_GET_UL_64QAM = ENG_AT_TEST;
    String ENG_AT_SET_CPC = ENG_AT_TEST;
    String ENG_AT_GET_CPC = ENG_AT_TEST;
    String ENG_AT_SET_16QAM = ENG_AT_TEST;
    String ENG_AT_GET_16QAM = ENG_AT_TEST;
    String ENG_AT_SET_DBHSDPA = ENG_AT_TEST;
    String ENG_AT_GET_DBHSDPA = ENG_AT_TEST;
    String ENG_AT_SET_DCHSDPA = ENG_AT_TEST;
    String ENG_AT_GET_DCHSDPA = ENG_AT_TEST;
    String ENG_AT_SET_IMPROVED_12_UL = ENG_AT_TEST;
    String ENG_AT_GET_IMPROVED_12_UL = ENG_AT_TEST;
    String ENG_AT_SET_EFACH = ENG_AT_TEST;
    String ENG_AT_GET_EFACH = ENG_AT_TEST;
    String ENG_AT_SET_EDRX = ENG_AT_TEST;
    String ENG_AT_GET_EDRX = ENG_AT_TEST;
    String ENG_AT_SET_ERACH = ENG_AT_TEST;
    String ENG_AT_GET_ERACH = ENG_AT_TEST;
    String ENG_AT_SET_SNOW3G = ENG_AT_TEST;
    String ENG_AT_GET_SNOW3G = ENG_AT_TEST;
    String ENG_AT_SET_W_DIVERSITY = ENG_AT_TEST;
    String ENG_AT_GET_W_DIVERSITY = ENG_AT_TEST;
    String ENG_AT_SET_FAST_DORMANCY = ENG_AT_TEST;
    String ENG_AT_GET_FAST_DORMANCY = ENG_AT_TEST;
    String ENG_AT_SET_CBS = ENG_AT_TEST;
    String ENG_AT_GET_CBS = ENG_AT_TEST;
    String ENG_AT_SET_UL_HSDPA = ENG_AT_TEST;
    String ENG_AT_GET_UL_HSDPA = ENG_AT_TEST;
    String ENG_AT_SET_UL_HSUPA = ENG_AT_TEST;
    String ENG_AT_GET_UL_HSUPA = ENG_AT_TEST;
    String ENG_AT_SET_TYPE3I = ENG_AT_TEST;
    String ENG_AT_GET_TYPE3I = ENG_AT_TEST;
    String ENG_AT_SET_EVS = "AT+SPENGMDVOLTE=12,1,1,";
    String ENG_AT_GET_EVS = "AT+SPENGMDVOLTE=12,0,1";//Voice codec type---1<value>: Bit 0: amr-wb; Bit 1: amr-nb; Bit 2: EVS
    String ENG_AT_SET_BSRVCC = "at+spengmdvolte = 24,1,";
    String ENG_AT_GET_BSRVCC = "at+spengmdvolte = 24,0";
    String ENG_AT_SET_ASRVCC = "at+spengmdvolte = 25,1,";
    String ENG_AT_GET_ASRVCC = "at+spengmdvolte = 25,0";
    String ENG_AT_SET_IPV6 = ENG_AT_TEST;
    String ENG_AT_GET_IPV6 = ENG_AT_TEST;
    // ue_cap
    String ENG_AT_SET_ETB = "AT+SPCORESIGHT=";
    String ENG_AT_GET_ETB = "AT+SPCORESIGHT?";
    /* SPRD 838786 : PDN Test Mode add @{ */
    String ENG_AT_SET_CGD = "AT+CGDSCONT=";
    String ENG_AT_SET_CGE = "AT+CGEQOS=";
    String ENG_AT_SET_CGT = "AT+CGTFT=";
    String ENG_AT_SET_CGA = "AT+CGACT=";
    /* }@ */
    /* SPRD:838473 mbsfn swtich @{ */
    String ENG_AT_MBSFN = "at+cembmscfg=";
    /* }@ */
    /* SPRD BUG 838388 - Frequency Point test @{ */
    String ENG_AT_SFUN = "AT+SFUN=";
    String ENG_AT_SPCLEANINFO = "AT+SPCLEANINFO=4";
    String ENG_AT_SETSIM = "AT+SPACTCARD=";
    /* }@ */
    /* SPRD: 871064 - Delta nv @{ */
    String ENG_AT_SET_SPAPC_DISABLE = "AT+SPAPC=0,0";
    String ENG_AT_SET_SPAPC_ENABLE = "AT+SPAPC=1,0";
    String ENG_AT_GET_SPAPC = "AT+SPAPC?";
    String ENG_AT_SET_SPOPRCFGNV = "AT+SPOPRCFGNV=";
    /* }@ */
    /* sprd 898844 : Volte Settings @{ */
    String ENG_AT_SPVOLTEENG = "AT+SPVOLTEENG=";
    String ENG_AT_GET_SMS_IP = "AT+CASIMS?";
    String ENG_AT_SET_SMS_IP = "AT+CASIMS=";
    String ENG_AT_GET_WHITE_LIST = "AT+SPVOLTEWL?";
    /* }@ */
    /* SPRD: 922313 - lvds dump @{ */
    String ENG_SET_AT_LVDS_DUMP = "AT^LVDSDUMP=";
    String ENG_GET_AT_LVDS_DUMP = "AT^LVDSDUMP?";
    /* }@ */
    /* sprd 958489  : W_G network mode @{ */
    String ENG_AT_SET_WCDMA_MODEM = "AT+SPTESTMODEM=";
    String ENG_AT_GET_WCDMA_MODEM = "AT+SPTESTMODEM?";
    /* }@ */
    /* SPRD 1020953: C2K feature @{ */
    String ENG_AT_SET_DELTANV_NAME = "AT+SPOPRCFGNV=";
    String ENG_AT_GET_DELTANV_NAME = "AT+SPOPRCFGNV=?";

    String ENG_AT_GET_SPC2KFRQ = "AT+SPC2KFRQ?";
    String ENG_AT_SET_SPC2KFRQ = "AT+SPC2KFRQ=";
    String ENG_AT_SPC2K_DUMMYPARA = "AT_SPC2KDUMMYPARA=";
    /* }@ */
    String ENG_AT_GET_SPC2K_RAT = "at+spc2ksetrat?";
    String ENG_AT_SET_SPC2K_RAT = "at+spc2ksetrat=1";

    String ENG_AT_GET_SPCAS_PARAM = "AT+SPCASPARAM?";
    String ENG_AT_SET_SPCAS_PARAM = "AT+SPCASPARAM=";

    String ENG_AT_SET_DPDT_PARAM = "At+SPDPDTENABLE=";
    String ENG_AT_SET_WASDUMMY_PARAM = "At+SPWASDUMMY=";

    /** sprd 1002440  : 5G mode
     * UNISOC: Bug1381637 Adapt and modify according to the underlying NR support. @{ */
    String ENG_AT_NR_SERVICE_CELL = "AT+SPENGMD=0,14,1";
    String ENG_AT_NR_DIFF_ADJACENT_CELL = "AT+SPENGMD=0,14,2";
    String ENG_AT_SPENGMDVOLTE_240 = "AT+SPENGMDVOLTE=24,0";
    String ENG_AT_SPENGMD_060 = "AT+SPENGMD=0,6,0";
    String ENG_AT_SPENGMDVOLTE_290 = "AT+SPENGMDVOLTE=29,0";
    String ENG_AT_NR_ADJACENT_CELL = "AT+SPENGMD=0,14,3";
    String ENG_AT_SPENGMD_0125 = "AT+SPENGMD=0,12,5";
    String ENG_AT_GET_NR_BAND = "AT+SPLBAND=3";
    String ENG_AT_GET_SUPPORT_NR_BAND = "AT+SP5GCMDS=\"get nr support_band\"";
    String ENG_AT_GET_QRM = "AT+SP5GCMDS=\"get nr synch_param\",40";
    String ENG_AT_SET_QRM = "AT+SP5GCMDS=\"set nr param\",31,";
    String ENG_AT_SET_NR_BAND = "AT+SPLBAND=2,";
    /* }@ */
    String ENG_AT_SET_NR_CAP = "AT+SPCAPABILITY=51,1,";
    String ENG_AT_GET_NR_CAP = "AT+SPCAPABILITY=51,0";

    String ENG_AT_SET_EMODE = "AT+CEMODE=";
    String ENG_AT_SET_EUS = "AT+CEUS=";
    String ENG_AT_GET_EUS = "AT+CEUS?";

    void ParseAtResponse(String Response);

    String ENG_AT_SET_LAS_DUMMY = "AT+SPLASDUMMY=";
    String ENG_AT_SET_NAS_DUMMY = "AT+SPNASDUMMY=";
}
