package com.ali.address911;

public class Address911Constant {

	public interface CancelCode {
		static final int NETWORK_ERROR = 1001;
		static final int TOKEN_IS_EMPTY = 1002;

	}
    //Define by VZW
	public interface CompleteCode {
		static final String COMPLETE_CODE_SUCCESS = "00000";
		static final String COMPLETE_CODE_INVALID_U_PWD = "12001";
		static final String COMPLETE_CODE_INVALID_CLIENT_ID = "12002";
		static final String COMPLETE_CODE_INVALID_FEATURE_NOT_AVAILABLE = "12003";
		static final String COMPLETE_CODE_INVALID_INTERNAL_SEVER_ERROR = "13000";
		static final String COMPLETE_CODE_INVALID_SERVICE_UNAVAILABLE = "13001";
		static final String COMPLETE_CODE_INVALID_MDN = "13002";
		static final String COMPLETE_CODE_INVALID_ADDRESS = "13003";
		static final String COMPLETE_CODE_INVALID_REG_TYPE = "13004";
		static final String COMPLETE_CODE_INVALID_SVCNUM = "13005";
		static final String COMPLETE_CODE_INVALID_SUB_SVCNUM = "13006";
		static final String COMPLETE_CODE_INVALID_DEVICE_ID = "13007";
		static final String COMPLETE_CODE_INVALID_ALTERNATE_ADDRESS_FOUND = "13008";
		static final String COMPLETE_CODE_INVALID_NO_ADDRESS_FOUND = "13009";
		static final String COMPLETE_CODE_INVALID = "99999";
	}

    public interface ReqType {
        String QUERY = "QUERY";
        String ADD = "ADD";
    }

    public interface NetWorkResponse{

    }

    public interface SvcHdr{
        String SVCHDR = "ns2:SvcHd";
        String SVCINFO = "ns2:SvcInfo";
        String SVCNM = "ns2:svcNm";
        String SUBSVCNM = "ns2:subSvcNm";
        String ERRINFO = "ns2:ErrInfo";
        String ERRCODE = "ns2:errCd";
        String EERLVL = "ns2:errLvl";
        String ERRORMESSAGE = "ns2:errMsg";
    }
    public interface SvcBdyTag{
        String SVCBDY = "SvcBdy";
        String SVCREQ = "SvcReq";
        String REQUEST_ID = "requestId";
        String APP_TOKEN = "appToken";
        String USER_DETAIL_LIST = "userDetailList";
        String USER_DETAIL = "UserDetail";
        String REQUEST_TYPE = "reqType";
        String ADDRESS = "Address";
        String MDN = "mdn";
        String IMEI = "imei";
        String ALTERNATE_ADDRESSLIST = "AlternateAddressList";
        String ALTADDRESS_DETAIL = "AltAddressDetail";
    }

    public interface Address{
        String HOUSENUMBER = "houseNumber";
        String ROAD = "road";
        String LOCATION = "location";
        String CITY = "city";
        String STATE = "state";
        String ZIP = "zip";
        String COUNTRY = "country";
    }


}
