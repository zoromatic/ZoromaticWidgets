package com.zoromatic.widgets;

class WeatherConditionPriorities {

	static final int PRIORITY_CLOUDS 		= 0;
	static final int PRIORITY_ATMOSPHERE 	= 1;
	static final int PRIORITY_DRIZZLE 		= 2;
	static final int PRIORITY_SNOW 			= 3;
	static final int PRIORITY_RAIN 			= 4;
	static final int PRIORITY_THUNDERSTORM 	= 5;
	static final int PRIORITY_EXTREME 		= 6;
    
    private WeatherConditionPriorities() {
      
    }
}

class WeatherIcon {
	String iconName;
	int iconId;
	boolean bDay;
	int altIconId;
	
	public WeatherIcon(String name, int id, boolean day, int altId) {
		iconId = id;
		iconName = name;
		bDay = day;
		altIconId = altId;
	}
}


public class WeatherConditions {	
	
	int THUNDERSTORM_WITH_LIGHT_RAIN 	= 200; 
	int THUNDERSTORM_WITH_RAIN 			= 201; 
	int THUNDERSTORM_WITH_HEAVY_RAIN 	= 202; 
	int LIGHT_THUNDERSTORM 				= 210; 
	int THUNDERSTORM 					= 211; 
	int HEAVY_THUNDERSTORM 				= 212; 
	int RAGGED_THUNDERSTORM 			= 221; 
	int THUNDERSTORM_WITH_LIGHT_DRIZZLE = 230;
	int THUNDERSTORM_WITH_DRIZZLE 		= 231;
	int THUNDERSTORM_WITH_HEAVY_DRIZZLE = 232; 

	int LIGHT_INTENSITY_DRIZZLE 		= 300; 
	int DRIZZLE 						= 301; 
	int HEAVY_INTENSITY_DRIZZLE 		= 302; 
	int LIGHT_INTENSITY_DRIZZLE_RAIN 	= 310; 
	int DRIZZLE_RAIN 					= 311; 
	int HEAVY_INTENSITY_DRIZZLE_RAIN 	= 312; 
	int SHOWER_DRIZZLE 					= 321; 

	int LIGHT_RAIN 						= 500; 
	int MODERATE_RAIN 					= 501; 
	int HEAVY_INTENSITY_RAIN 			= 502; 
	int VERY_HEAVY_RAIN 				= 503; 
	int EXTREME_RAIN 					= 504; 
	int FREEZING_RAIN 					= 511; 
	int LIGHT_INTENSITY_SHOWER_RAIN 	= 520; 
	int SHOWER_RAIN 					= 521; 
	int HEAVY_INTENSITY_SHOWER_RAIN 	= 522; 

	int LIGHT_SNOW 	= 600; 
	int SNOW 		= 601; 
	int HEAVY_SNOW 	= 602; 
	int SLEET 		= 611; 
	int SHOWER_SNOW = 621; 

	int MIST 				= 701; 
	int SMOKE 				= 711; 
	int HAZE 				= 721; 
	int SAND_DUST_WHIRLS 	= 731; 
	int FOG 				= 741; 

	int SKY_IS_CLEAR 		= 800; 
	int FEW_CLOUDS 			= 801; 
	int SCATTERED_CLOUDS 	= 802; 
	int BROKEN_CLOUDS 		= 803; 
	int OVERCAST_CLOUDS 	= 804; 

	int TORNADO 		= 900; 
	int TROPICAL_STORM 	= 901; 
	int HURRICANE 		= 902; 
	int COLD 			= 903; 
	int HOT 			= 904; 
	int WINDY 			= 905; 
	int HAIL 			= 906;
	
    public WeatherIcon[] m_ImageArrTick = 
	{
    	new WeatherIcon("01d", R.drawable.tick_weather_01d, true, R.drawable.tick_weather_01n),
    	new WeatherIcon("01n", R.drawable.tick_weather_01n, false, R.drawable.tick_weather_01d),
    	new WeatherIcon("02d", R.drawable.tick_weather_02d, true, R.drawable.tick_weather_02n),
    	new WeatherIcon("02n", R.drawable.tick_weather_02n, false, R.drawable.tick_weather_02d),
    	new WeatherIcon("03d", R.drawable.tick_weather_03d, true, R.drawable.tick_weather_03n),
    	new WeatherIcon("03n", R.drawable.tick_weather_03n, false, R.drawable.tick_weather_03d),
    	new WeatherIcon("04d", R.drawable.tick_weather_04d, true, R.drawable.tick_weather_04n),
    	new WeatherIcon("04n", R.drawable.tick_weather_04n, false, R.drawable.tick_weather_04d),
    	new WeatherIcon("09d", R.drawable.tick_weather_09d, true, R.drawable.tick_weather_09n),
    	new WeatherIcon("09n", R.drawable.tick_weather_09n, false, R.drawable.tick_weather_09d),
    	new WeatherIcon("10d", R.drawable.tick_weather_10d, true, R.drawable.tick_weather_10n),
    	new WeatherIcon("10n", R.drawable.tick_weather_10n, false, R.drawable.tick_weather_10d),
    	new WeatherIcon("11d", R.drawable.tick_weather_11d, true, R.drawable.tick_weather_11n),
    	new WeatherIcon("11n", R.drawable.tick_weather_11n, false, R.drawable.tick_weather_11d),
    	new WeatherIcon("13d", R.drawable.tick_weather_13d, true, R.drawable.tick_weather_13n),
    	new WeatherIcon("13n", R.drawable.tick_weather_13n, false, R.drawable.tick_weather_13d),
    	new WeatherIcon("50d", R.drawable.tick_weather_50d, true, R.drawable.tick_weather_50n),
    	new WeatherIcon("50n", R.drawable.tick_weather_50n, false, R.drawable.tick_weather_50d),
    	new WeatherIcon("r", R.drawable.tick_weather_r, true, R.drawable.tick_weather_r),
    	new WeatherIcon("r", R.drawable.tick_weather_r, false, R.drawable.tick_weather_r),
    	new WeatherIcon("sn50", R.drawable.tick_weather_sn50, true, R.drawable.tick_weather_sn50),
    	new WeatherIcon("sn50", R.drawable.tick_weather_sn50, false, R.drawable.tick_weather_sn50),
    	new WeatherIcon("t50", R.drawable.tick_weather_t50, true, R.drawable.tick_weather_t50),
    	new WeatherIcon("t50", R.drawable.tick_weather_t50, false, R.drawable.tick_weather_t50),
    	new WeatherIcon("w50", R.drawable.tick_weather_w50, true, R.drawable.tick_weather_w50),
    	new WeatherIcon("w50", R.drawable.tick_weather_w50, false, R.drawable.tick_weather_w50)
	};
    
    public WeatherIcon[] m_ImageArrTouch = 
	{
    	new WeatherIcon("01d", R.drawable.touch_weather_01d, true, R.drawable.touch_weather_01n),
    	new WeatherIcon("01n", R.drawable.touch_weather_01n, false, R.drawable.touch_weather_01d),
    	new WeatherIcon("02d", R.drawable.touch_weather_02d, true, R.drawable.touch_weather_02n),
    	new WeatherIcon("02n", R.drawable.touch_weather_02n, false, R.drawable.touch_weather_02d),
    	new WeatherIcon("03d", R.drawable.touch_weather_03d, true, R.drawable.touch_weather_03n),
    	new WeatherIcon("03n", R.drawable.touch_weather_03n, false, R.drawable.touch_weather_03d),
    	new WeatherIcon("04d", R.drawable.touch_weather_04d, true, R.drawable.touch_weather_04n),
    	new WeatherIcon("04n", R.drawable.touch_weather_04n, false, R.drawable.touch_weather_04d),
    	new WeatherIcon("09d", R.drawable.touch_weather_09d, true, R.drawable.touch_weather_09n),
    	new WeatherIcon("09n", R.drawable.touch_weather_09n, false, R.drawable.touch_weather_09d),
    	new WeatherIcon("10d", R.drawable.touch_weather_10d, true, R.drawable.touch_weather_10n),
    	new WeatherIcon("10n", R.drawable.touch_weather_10n, false, R.drawable.touch_weather_10d),
    	new WeatherIcon("11d", R.drawable.touch_weather_11d, true, R.drawable.touch_weather_11n),
    	new WeatherIcon("11n", R.drawable.touch_weather_11n, false, R.drawable.touch_weather_11d),
    	new WeatherIcon("13d", R.drawable.touch_weather_13d, true, R.drawable.touch_weather_13n),
    	new WeatherIcon("13n", R.drawable.touch_weather_13n, false, R.drawable.touch_weather_13d),
    	new WeatherIcon("50d", R.drawable.touch_weather_50d, true, R.drawable.touch_weather_50n),
    	new WeatherIcon("50n", R.drawable.touch_weather_50n, false, R.drawable.touch_weather_50d),
    	new WeatherIcon("r", R.drawable.touch_weather_r, true, R.drawable.touch_weather_r),
    	new WeatherIcon("r", R.drawable.touch_weather_r, false, R.drawable.touch_weather_r),
    	new WeatherIcon("sn50", R.drawable.touch_weather_sn50, true, R.drawable.touch_weather_sn50),
    	new WeatherIcon("sn50", R.drawable.touch_weather_sn50, false, R.drawable.touch_weather_sn50),
    	new WeatherIcon("t50", R.drawable.touch_weather_t50, true, R.drawable.touch_weather_t50),
    	new WeatherIcon("t50", R.drawable.touch_weather_t50, false, R.drawable.touch_weather_t50),
    	new WeatherIcon("w50", R.drawable.touch_weather_w50, true, R.drawable.touch_weather_w50),
    	new WeatherIcon("w50", R.drawable.touch_weather_w50, false, R.drawable.touch_weather_w50)
	};
    
    public WeatherIcon[] m_ImageArrIconSet = 
	{
    	new WeatherIcon("01d", R.drawable.icon_set_weather_01d, true, R.drawable.icon_set_weather_01n),
    	new WeatherIcon("01n", R.drawable.icon_set_weather_01n, false, R.drawable.icon_set_weather_01d),
    	new WeatherIcon("02d", R.drawable.icon_set_weather_02d, true, R.drawable.icon_set_weather_02n),
    	new WeatherIcon("02n", R.drawable.icon_set_weather_02n, false, R.drawable.icon_set_weather_02d),
    	new WeatherIcon("03d", R.drawable.icon_set_weather_03d, true, R.drawable.icon_set_weather_03n),
    	new WeatherIcon("03n", R.drawable.icon_set_weather_03n, false, R.drawable.icon_set_weather_03d),
    	new WeatherIcon("04d", R.drawable.icon_set_weather_04d, true, R.drawable.icon_set_weather_04n),
    	new WeatherIcon("04n", R.drawable.icon_set_weather_04n, false, R.drawable.icon_set_weather_04d),
    	new WeatherIcon("09d", R.drawable.icon_set_weather_09d, true, R.drawable.icon_set_weather_09n),
    	new WeatherIcon("09n", R.drawable.icon_set_weather_09n, false, R.drawable.icon_set_weather_09d),
    	new WeatherIcon("10d", R.drawable.icon_set_weather_10d, true, R.drawable.icon_set_weather_10n),
    	new WeatherIcon("10n", R.drawable.icon_set_weather_10n, false, R.drawable.icon_set_weather_10d),
    	new WeatherIcon("11d", R.drawable.icon_set_weather_11d, true, R.drawable.icon_set_weather_11n),
    	new WeatherIcon("11n", R.drawable.icon_set_weather_11n, false, R.drawable.icon_set_weather_11d),
    	new WeatherIcon("13d", R.drawable.icon_set_weather_13d, true, R.drawable.icon_set_weather_13n),
    	new WeatherIcon("13n", R.drawable.icon_set_weather_13n, false, R.drawable.icon_set_weather_13d),
    	new WeatherIcon("50d", R.drawable.icon_set_weather_50d, true, R.drawable.icon_set_weather_50n),
    	new WeatherIcon("50n", R.drawable.icon_set_weather_50n, false, R.drawable.icon_set_weather_50d),
    	new WeatherIcon("r", R.drawable.icon_set_weather_r, true, R.drawable.icon_set_weather_r),
    	new WeatherIcon("r", R.drawable.icon_set_weather_r, false, R.drawable.icon_set_weather_r),
    	new WeatherIcon("sn50", R.drawable.icon_set_weather_sn50, true, R.drawable.icon_set_weather_sn50),
    	new WeatherIcon("sn50", R.drawable.icon_set_weather_sn50, false, R.drawable.icon_set_weather_sn50),
    	new WeatherIcon("t50", R.drawable.icon_set_weather_t50, true, R.drawable.icon_set_weather_t50),
    	new WeatherIcon("t50", R.drawable.icon_set_weather_t50, false, R.drawable.icon_set_weather_t50),
    	new WeatherIcon("w50", R.drawable.icon_set_weather_w50, true, R.drawable.icon_set_weather_w50),
    	new WeatherIcon("w50", R.drawable.icon_set_weather_w50, false, R.drawable.icon_set_weather_w50)
	};
    
    public WeatherIcon[] m_ImageArrWeezle = 
	{
    	new WeatherIcon("01d", R.drawable.weezle_weather_01d, true, R.drawable.weezle_weather_01n),
    	new WeatherIcon("01n", R.drawable.weezle_weather_01n, false, R.drawable.weezle_weather_01d),
    	new WeatherIcon("02d", R.drawable.weezle_weather_02d, true, R.drawable.weezle_weather_02n),
    	new WeatherIcon("02n", R.drawable.weezle_weather_02n, false, R.drawable.weezle_weather_02d),
    	new WeatherIcon("03d", R.drawable.weezle_weather_03d, true, R.drawable.weezle_weather_03n),
    	new WeatherIcon("03n", R.drawable.weezle_weather_03n, false, R.drawable.weezle_weather_03d),
    	new WeatherIcon("04d", R.drawable.weezle_weather_04d, true, R.drawable.weezle_weather_04n),
    	new WeatherIcon("04n", R.drawable.weezle_weather_04n, false, R.drawable.weezle_weather_04d),
    	new WeatherIcon("09d", R.drawable.weezle_weather_09d, true, R.drawable.weezle_weather_09n),
    	new WeatherIcon("09n", R.drawable.weezle_weather_09n, false, R.drawable.weezle_weather_09d),
    	new WeatherIcon("10d", R.drawable.weezle_weather_10d, true, R.drawable.weezle_weather_10n),
    	new WeatherIcon("10n", R.drawable.weezle_weather_10n, false, R.drawable.weezle_weather_10d),
    	new WeatherIcon("11d", R.drawable.weezle_weather_11d, true, R.drawable.weezle_weather_11n),
    	new WeatherIcon("11n", R.drawable.weezle_weather_11n, false, R.drawable.weezle_weather_11d),
    	new WeatherIcon("13d", R.drawable.weezle_weather_13d, true, R.drawable.weezle_weather_13n),
    	new WeatherIcon("13n", R.drawable.weezle_weather_13n, false, R.drawable.weezle_weather_13d),
    	new WeatherIcon("50d", R.drawable.weezle_weather_50d, true, R.drawable.weezle_weather_50n),
    	new WeatherIcon("50n", R.drawable.weezle_weather_50n, false, R.drawable.weezle_weather_50d),
    	new WeatherIcon("r", R.drawable.weezle_weather_r, true, R.drawable.weezle_weather_r),
    	new WeatherIcon("r", R.drawable.weezle_weather_r, false, R.drawable.weezle_weather_r),
    	new WeatherIcon("sn50", R.drawable.weezle_weather_sn50, true, R.drawable.weezle_weather_sn50),
    	new WeatherIcon("sn50", R.drawable.weezle_weather_sn50, false, R.drawable.weezle_weather_sn50),
    	new WeatherIcon("t50", R.drawable.weezle_weather_t50, true, R.drawable.weezle_weather_t50),
    	new WeatherIcon("t50", R.drawable.weezle_weather_t50, false, R.drawable.weezle_weather_t50),
    	new WeatherIcon("w50", R.drawable.weezle_weather_w50, true, R.drawable.weezle_weather_w50),
    	new WeatherIcon("w50", R.drawable.weezle_weather_w50, false, R.drawable.weezle_weather_w50)
	};
    
    public WeatherIcon[] m_ImageArrSimple = 
    	{
        	new WeatherIcon("01d", R.drawable.simple_weather_01d, true, R.drawable.simple_weather_01n),
        	new WeatherIcon("01n", R.drawable.simple_weather_01n, false, R.drawable.simple_weather_01d),
        	new WeatherIcon("02d", R.drawable.simple_weather_02d, true, R.drawable.simple_weather_02n),
        	new WeatherIcon("02n", R.drawable.simple_weather_02n, false, R.drawable.simple_weather_02d),
        	new WeatherIcon("03d", R.drawable.simple_weather_03d, true, R.drawable.simple_weather_03n),
        	new WeatherIcon("03n", R.drawable.simple_weather_03n, false, R.drawable.simple_weather_03d),
        	new WeatherIcon("04d", R.drawable.simple_weather_04d, true, R.drawable.simple_weather_04n),
        	new WeatherIcon("04n", R.drawable.simple_weather_04n, false, R.drawable.simple_weather_04d),
        	new WeatherIcon("09d", R.drawable.simple_weather_09d, true, R.drawable.simple_weather_09n),
        	new WeatherIcon("09n", R.drawable.simple_weather_09n, false, R.drawable.simple_weather_09d),
        	new WeatherIcon("10d", R.drawable.simple_weather_10d, true, R.drawable.simple_weather_10n),
        	new WeatherIcon("10n", R.drawable.simple_weather_10n, false, R.drawable.simple_weather_10d),
        	new WeatherIcon("11d", R.drawable.simple_weather_11d, true, R.drawable.simple_weather_11n),
        	new WeatherIcon("11n", R.drawable.simple_weather_11n, false, R.drawable.simple_weather_11d),
        	new WeatherIcon("13d", R.drawable.simple_weather_13d, true, R.drawable.simple_weather_13n),
        	new WeatherIcon("13n", R.drawable.simple_weather_13n, false, R.drawable.simple_weather_13d),
        	new WeatherIcon("50d", R.drawable.simple_weather_50d, true, R.drawable.simple_weather_50n),
        	new WeatherIcon("50n", R.drawable.simple_weather_50n, false, R.drawable.simple_weather_50d),
        	new WeatherIcon("r", R.drawable.simple_weather_r, true, R.drawable.simple_weather_r),
        	new WeatherIcon("r", R.drawable.simple_weather_r, false, R.drawable.simple_weather_r),
        	new WeatherIcon("sn50", R.drawable.simple_weather_sn50, true, R.drawable.simple_weather_sn50),
        	new WeatherIcon("sn50", R.drawable.simple_weather_sn50, false, R.drawable.simple_weather_sn50),
        	new WeatherIcon("t50", R.drawable.simple_weather_t50, true, R.drawable.simple_weather_t50),
        	new WeatherIcon("t50", R.drawable.simple_weather_t50, false, R.drawable.simple_weather_t50),
        	new WeatherIcon("w50", R.drawable.simple_weather_w50, true, R.drawable.simple_weather_w50),
        	new WeatherIcon("w50", R.drawable.simple_weather_w50, false, R.drawable.simple_weather_w50)
    	};
    
    public WeatherIcon[] m_ImageArrNovacons = 
    	{
        	new WeatherIcon("01d", R.drawable.novacons_weather_01d, true, R.drawable.novacons_weather_01n),
        	new WeatherIcon("01n", R.drawable.novacons_weather_01n, false, R.drawable.novacons_weather_01d),
        	new WeatherIcon("02d", R.drawable.novacons_weather_02d, true, R.drawable.novacons_weather_02n),
        	new WeatherIcon("02n", R.drawable.novacons_weather_02n, false, R.drawable.novacons_weather_02d),
        	new WeatherIcon("03d", R.drawable.novacons_weather_03d, true, R.drawable.novacons_weather_03n),
        	new WeatherIcon("03n", R.drawable.novacons_weather_03n, false, R.drawable.novacons_weather_03d),
        	new WeatherIcon("04d", R.drawable.novacons_weather_04d, true, R.drawable.novacons_weather_04n),
        	new WeatherIcon("04n", R.drawable.novacons_weather_04n, false, R.drawable.novacons_weather_04d),
        	new WeatherIcon("09d", R.drawable.novacons_weather_09d, true, R.drawable.novacons_weather_09n),
        	new WeatherIcon("09n", R.drawable.novacons_weather_09n, false, R.drawable.novacons_weather_09d),
        	new WeatherIcon("10d", R.drawable.novacons_weather_10d, true, R.drawable.novacons_weather_10n),
        	new WeatherIcon("10n", R.drawable.novacons_weather_10n, false, R.drawable.novacons_weather_10d),
        	new WeatherIcon("11d", R.drawable.novacons_weather_11d, true, R.drawable.novacons_weather_11n),
        	new WeatherIcon("11n", R.drawable.novacons_weather_11n, false, R.drawable.novacons_weather_11d),
        	new WeatherIcon("13d", R.drawable.novacons_weather_13d, true, R.drawable.novacons_weather_13n),
        	new WeatherIcon("13n", R.drawable.novacons_weather_13n, false, R.drawable.novacons_weather_13d),
        	new WeatherIcon("50d", R.drawable.novacons_weather_50d, true, R.drawable.novacons_weather_50n),
        	new WeatherIcon("50n", R.drawable.novacons_weather_50n, false, R.drawable.novacons_weather_50d),
        	new WeatherIcon("r", R.drawable.novacons_weather_r, true, R.drawable.novacons_weather_r),
        	new WeatherIcon("r", R.drawable.novacons_weather_r, false, R.drawable.novacons_weather_r),
        	new WeatherIcon("sn50", R.drawable.novacons_weather_sn50, true, R.drawable.novacons_weather_sn50),
        	new WeatherIcon("sn50", R.drawable.novacons_weather_sn50, false, R.drawable.novacons_weather_sn50),
        	new WeatherIcon("t50", R.drawable.novacons_weather_t50, true, R.drawable.novacons_weather_t50),
        	new WeatherIcon("t50", R.drawable.novacons_weather_t50, false, R.drawable.novacons_weather_t50),
        	new WeatherIcon("w50", R.drawable.novacons_weather_w50, true, R.drawable.novacons_weather_w50),
        	new WeatherIcon("w50", R.drawable.novacons_weather_w50, false, R.drawable.novacons_weather_w50)
    	};
    
    public WeatherIcon[] m_ImageArrSticker = 
    	{
        	new WeatherIcon("01d", R.drawable.sticker_weather_01d, true, R.drawable.sticker_weather_01n),
        	new WeatherIcon("01n", R.drawable.sticker_weather_01n, false, R.drawable.sticker_weather_01d),
        	new WeatherIcon("02d", R.drawable.sticker_weather_02d, true, R.drawable.sticker_weather_02n),
        	new WeatherIcon("02n", R.drawable.sticker_weather_02n, false, R.drawable.sticker_weather_02d),
        	new WeatherIcon("03d", R.drawable.sticker_weather_03d, true, R.drawable.sticker_weather_03n),
        	new WeatherIcon("03n", R.drawable.sticker_weather_03n, false, R.drawable.sticker_weather_03d),
        	new WeatherIcon("04d", R.drawable.sticker_weather_04d, true, R.drawable.sticker_weather_04n),
        	new WeatherIcon("04n", R.drawable.sticker_weather_04n, false, R.drawable.sticker_weather_04d),
        	new WeatherIcon("09d", R.drawable.sticker_weather_09d, true, R.drawable.sticker_weather_09n),
        	new WeatherIcon("09n", R.drawable.sticker_weather_09n, false, R.drawable.sticker_weather_09d),
        	new WeatherIcon("10d", R.drawable.sticker_weather_10d, true, R.drawable.sticker_weather_10n),
        	new WeatherIcon("10n", R.drawable.sticker_weather_10n, false, R.drawable.sticker_weather_10d),
        	new WeatherIcon("11d", R.drawable.sticker_weather_11d, true, R.drawable.sticker_weather_11n),
        	new WeatherIcon("11n", R.drawable.sticker_weather_11n, false, R.drawable.sticker_weather_11d),
        	new WeatherIcon("13d", R.drawable.sticker_weather_13d, true, R.drawable.sticker_weather_13n),
        	new WeatherIcon("13n", R.drawable.sticker_weather_13n, false, R.drawable.sticker_weather_13d),
        	new WeatherIcon("50d", R.drawable.sticker_weather_50d, true, R.drawable.sticker_weather_50n),
        	new WeatherIcon("50n", R.drawable.sticker_weather_50n, false, R.drawable.sticker_weather_50d),
        	new WeatherIcon("r", R.drawable.sticker_weather_r, true, R.drawable.sticker_weather_r),
        	new WeatherIcon("r", R.drawable.sticker_weather_r, false, R.drawable.sticker_weather_r),
        	new WeatherIcon("sn50", R.drawable.sticker_weather_sn50, true, R.drawable.sticker_weather_sn50),
        	new WeatherIcon("sn50", R.drawable.sticker_weather_sn50, false, R.drawable.sticker_weather_sn50),
        	new WeatherIcon("t50", R.drawable.sticker_weather_t50, true, R.drawable.sticker_weather_t50),
        	new WeatherIcon("t50", R.drawable.sticker_weather_t50, false, R.drawable.sticker_weather_t50),
        	new WeatherIcon("w50", R.drawable.sticker_weather_w50, true, R.drawable.sticker_weather_w50),
        	new WeatherIcon("w50", R.drawable.sticker_weather_w50, false, R.drawable.sticker_weather_w50)
    	};
    
    public WeatherIcon[] m_ImageArrPlain = 
    	{
        	new WeatherIcon("01d", R.drawable.plain_weather_01d, true, R.drawable.plain_weather_01n),
        	new WeatherIcon("01n", R.drawable.plain_weather_01n, false, R.drawable.plain_weather_01d),
        	new WeatherIcon("02d", R.drawable.plain_weather_02d, true, R.drawable.plain_weather_02n),
        	new WeatherIcon("02n", R.drawable.plain_weather_02n, false, R.drawable.plain_weather_02d),
        	new WeatherIcon("03d", R.drawable.plain_weather_03d, true, R.drawable.plain_weather_03n),
        	new WeatherIcon("03n", R.drawable.plain_weather_03n, false, R.drawable.plain_weather_03d),
        	new WeatherIcon("04d", R.drawable.plain_weather_04d, true, R.drawable.plain_weather_04n),
        	new WeatherIcon("04n", R.drawable.plain_weather_04n, false, R.drawable.plain_weather_04d),
        	new WeatherIcon("09d", R.drawable.plain_weather_09d, true, R.drawable.plain_weather_09n),
        	new WeatherIcon("09n", R.drawable.plain_weather_09n, false, R.drawable.plain_weather_09d),
        	new WeatherIcon("10d", R.drawable.plain_weather_10d, true, R.drawable.plain_weather_10n),
        	new WeatherIcon("10n", R.drawable.plain_weather_10n, false, R.drawable.plain_weather_10d),
        	new WeatherIcon("11d", R.drawable.plain_weather_11d, true, R.drawable.plain_weather_11n),
        	new WeatherIcon("11n", R.drawable.plain_weather_11n, false, R.drawable.plain_weather_11d),
        	new WeatherIcon("13d", R.drawable.plain_weather_13d, true, R.drawable.plain_weather_13n),
        	new WeatherIcon("13n", R.drawable.plain_weather_13n, false, R.drawable.plain_weather_13d),
        	new WeatherIcon("50d", R.drawable.plain_weather_50d, true, R.drawable.plain_weather_50n),
        	new WeatherIcon("50n", R.drawable.plain_weather_50n, false, R.drawable.plain_weather_50d),
        	new WeatherIcon("r", R.drawable.plain_weather_r, true, R.drawable.plain_weather_r),
        	new WeatherIcon("r", R.drawable.plain_weather_r, false, R.drawable.plain_weather_r),
        	new WeatherIcon("sn50", R.drawable.plain_weather_sn50, true, R.drawable.plain_weather_sn50),
        	new WeatherIcon("sn50", R.drawable.plain_weather_sn50, false, R.drawable.plain_weather_sn50),
        	new WeatherIcon("t50", R.drawable.plain_weather_t50, true, R.drawable.plain_weather_t50),
        	new WeatherIcon("t50", R.drawable.plain_weather_t50, false, R.drawable.plain_weather_t50),
        	new WeatherIcon("w50", R.drawable.plain_weather_w50, true, R.drawable.plain_weather_w50),
        	new WeatherIcon("w50", R.drawable.plain_weather_w50, false, R.drawable.plain_weather_w50)
    	};
    
    public WeatherIcon[] m_ImageArrFlat = 
    	{
        	new WeatherIcon("01d", R.drawable.flat_weather_01d, true, R.drawable.flat_weather_01n),
        	new WeatherIcon("01n", R.drawable.flat_weather_01n, false, R.drawable.flat_weather_01d),
        	new WeatherIcon("02d", R.drawable.flat_weather_02d, true, R.drawable.flat_weather_02n),
        	new WeatherIcon("02n", R.drawable.flat_weather_02n, false, R.drawable.flat_weather_02d),
        	new WeatherIcon("03d", R.drawable.flat_weather_03d, true, R.drawable.flat_weather_03n),
        	new WeatherIcon("03n", R.drawable.flat_weather_03n, false, R.drawable.flat_weather_03d),
        	new WeatherIcon("04d", R.drawable.flat_weather_04d, true, R.drawable.flat_weather_04n),
        	new WeatherIcon("04n", R.drawable.flat_weather_04n, false, R.drawable.flat_weather_04d),
        	new WeatherIcon("09d", R.drawable.flat_weather_09d, true, R.drawable.flat_weather_09n),
        	new WeatherIcon("09n", R.drawable.flat_weather_09n, false, R.drawable.flat_weather_09d),
        	new WeatherIcon("10d", R.drawable.flat_weather_10d, true, R.drawable.flat_weather_10n),
        	new WeatherIcon("10n", R.drawable.flat_weather_10n, false, R.drawable.flat_weather_10d),
        	new WeatherIcon("11d", R.drawable.flat_weather_11d, true, R.drawable.flat_weather_11n),
        	new WeatherIcon("11n", R.drawable.flat_weather_11n, false, R.drawable.flat_weather_11d),
        	new WeatherIcon("13d", R.drawable.flat_weather_13d, true, R.drawable.flat_weather_13n),
        	new WeatherIcon("13n", R.drawable.flat_weather_13n, false, R.drawable.flat_weather_13d),
        	new WeatherIcon("50d", R.drawable.flat_weather_50d, true, R.drawable.flat_weather_50n),
        	new WeatherIcon("50n", R.drawable.flat_weather_50n, false, R.drawable.flat_weather_50d),
        	new WeatherIcon("r", R.drawable.flat_weather_r, true, R.drawable.flat_weather_r),
        	new WeatherIcon("r", R.drawable.flat_weather_r, false, R.drawable.flat_weather_r),
        	new WeatherIcon("sn50", R.drawable.flat_weather_sn50, true, R.drawable.flat_weather_sn50),
        	new WeatherIcon("sn50", R.drawable.flat_weather_sn50, false, R.drawable.flat_weather_sn50),
        	new WeatherIcon("t50", R.drawable.flat_weather_t50, true, R.drawable.flat_weather_t50),
        	new WeatherIcon("t50", R.drawable.flat_weather_t50, false, R.drawable.flat_weather_t50),
        	new WeatherIcon("w50", R.drawable.flat_weather_w50, true, R.drawable.flat_weather_w50),
        	new WeatherIcon("w50", R.drawable.flat_weather_w50, false, R.drawable.flat_weather_w50)
    	};
    
    public WeatherIcon[] m_ImageArrDvoid = 
    	{
        	new WeatherIcon("01d", R.drawable.dvoid_weather_01d, true, R.drawable.dvoid_weather_01n),
        	new WeatherIcon("01n", R.drawable.dvoid_weather_01n, false, R.drawable.dvoid_weather_01d),
        	new WeatherIcon("02d", R.drawable.dvoid_weather_02d, true, R.drawable.dvoid_weather_02n),
        	new WeatherIcon("02n", R.drawable.dvoid_weather_02n, false, R.drawable.dvoid_weather_02d),
        	new WeatherIcon("03d", R.drawable.dvoid_weather_03d, true, R.drawable.dvoid_weather_03n),
        	new WeatherIcon("03n", R.drawable.dvoid_weather_03n, false, R.drawable.dvoid_weather_03d),
        	new WeatherIcon("04d", R.drawable.dvoid_weather_04d, true, R.drawable.dvoid_weather_04n),
        	new WeatherIcon("04n", R.drawable.dvoid_weather_04n, false, R.drawable.dvoid_weather_04d),
        	new WeatherIcon("09d", R.drawable.dvoid_weather_09d, true, R.drawable.dvoid_weather_09n),
        	new WeatherIcon("09n", R.drawable.dvoid_weather_09n, false, R.drawable.dvoid_weather_09d),
        	new WeatherIcon("10d", R.drawable.dvoid_weather_10d, true, R.drawable.dvoid_weather_10n),
        	new WeatherIcon("10n", R.drawable.dvoid_weather_10n, false, R.drawable.dvoid_weather_10d),
        	new WeatherIcon("11d", R.drawable.dvoid_weather_11d, true, R.drawable.dvoid_weather_11n),
        	new WeatherIcon("11n", R.drawable.dvoid_weather_11n, false, R.drawable.dvoid_weather_11d),
        	new WeatherIcon("13d", R.drawable.dvoid_weather_13d, true, R.drawable.dvoid_weather_13n),
        	new WeatherIcon("13n", R.drawable.dvoid_weather_13n, false, R.drawable.dvoid_weather_13d),
        	new WeatherIcon("50d", R.drawable.dvoid_weather_50d, true, R.drawable.dvoid_weather_50n),
        	new WeatherIcon("50n", R.drawable.dvoid_weather_50n, false, R.drawable.dvoid_weather_50d),
        	new WeatherIcon("r", R.drawable.dvoid_weather_r, true, R.drawable.dvoid_weather_r),
        	new WeatherIcon("r", R.drawable.dvoid_weather_r, false, R.drawable.dvoid_weather_r),
        	new WeatherIcon("sn50", R.drawable.dvoid_weather_sn50, true, R.drawable.dvoid_weather_sn50),
        	new WeatherIcon("sn50", R.drawable.dvoid_weather_sn50, false, R.drawable.dvoid_weather_sn50),
        	new WeatherIcon("t50", R.drawable.dvoid_weather_t50, true, R.drawable.dvoid_weather_t50),
        	new WeatherIcon("t50", R.drawable.dvoid_weather_t50, false, R.drawable.dvoid_weather_t50),
        	new WeatherIcon("w50", R.drawable.dvoid_weather_w50, true, R.drawable.dvoid_weather_w50),
        	new WeatherIcon("w50", R.drawable.dvoid_weather_w50, false, R.drawable.dvoid_weather_w50)
    	};
    
    public WeatherIcon[] m_ImageArrIkonko = 
    	{
        	new WeatherIcon("01d", R.drawable.ikonko_weather_01d, true, R.drawable.ikonko_weather_01n),
        	new WeatherIcon("01n", R.drawable.ikonko_weather_01n, false, R.drawable.ikonko_weather_01d),
        	new WeatherIcon("02d", R.drawable.ikonko_weather_02d, true, R.drawable.ikonko_weather_02n),
        	new WeatherIcon("02n", R.drawable.ikonko_weather_02n, false, R.drawable.ikonko_weather_02d),
        	new WeatherIcon("03d", R.drawable.ikonko_weather_03d, true, R.drawable.ikonko_weather_03n),
        	new WeatherIcon("03n", R.drawable.ikonko_weather_03n, false, R.drawable.ikonko_weather_03d),
        	new WeatherIcon("04d", R.drawable.ikonko_weather_04d, true, R.drawable.ikonko_weather_04n),
        	new WeatherIcon("04n", R.drawable.ikonko_weather_04n, false, R.drawable.ikonko_weather_04d),
        	new WeatherIcon("09d", R.drawable.ikonko_weather_09d, true, R.drawable.ikonko_weather_09n),
        	new WeatherIcon("09n", R.drawable.ikonko_weather_09n, false, R.drawable.ikonko_weather_09d),
        	new WeatherIcon("10d", R.drawable.ikonko_weather_10d, true, R.drawable.ikonko_weather_10n),
        	new WeatherIcon("10n", R.drawable.ikonko_weather_10n, false, R.drawable.ikonko_weather_10d),
        	new WeatherIcon("11d", R.drawable.ikonko_weather_11d, true, R.drawable.ikonko_weather_11n),
        	new WeatherIcon("11n", R.drawable.ikonko_weather_11n, false, R.drawable.ikonko_weather_11d),
        	new WeatherIcon("13d", R.drawable.ikonko_weather_13d, true, R.drawable.ikonko_weather_13n),
        	new WeatherIcon("13n", R.drawable.ikonko_weather_13n, false, R.drawable.ikonko_weather_13d),
        	new WeatherIcon("50d", R.drawable.ikonko_weather_50d, true, R.drawable.ikonko_weather_50n),
        	new WeatherIcon("50n", R.drawable.ikonko_weather_50n, false, R.drawable.ikonko_weather_50d),
        	new WeatherIcon("r", R.drawable.ikonko_weather_r, true, R.drawable.ikonko_weather_r),
        	new WeatherIcon("r", R.drawable.ikonko_weather_r, false, R.drawable.ikonko_weather_r),
        	new WeatherIcon("sn50", R.drawable.ikonko_weather_sn50, true, R.drawable.ikonko_weather_sn50),
        	new WeatherIcon("sn50", R.drawable.ikonko_weather_sn50, false, R.drawable.ikonko_weather_sn50),
        	new WeatherIcon("t50", R.drawable.ikonko_weather_t50, true, R.drawable.ikonko_weather_t50),
        	new WeatherIcon("t50", R.drawable.ikonko_weather_t50, false, R.drawable.ikonko_weather_t50),
        	new WeatherIcon("w50", R.drawable.ikonko_weather_w50, true, R.drawable.ikonko_weather_w50),
        	new WeatherIcon("w50", R.drawable.ikonko_weather_w50, false, R.drawable.ikonko_weather_w50)
    	};
    
    public WeatherIcon[] m_ImageArrSmooth = 
    	{
        	new WeatherIcon("01d", R.drawable.smooth_weather_01d, true, R.drawable.smooth_weather_01n),
        	new WeatherIcon("01n", R.drawable.smooth_weather_01n, false, R.drawable.smooth_weather_01d),
        	new WeatherIcon("02d", R.drawable.smooth_weather_02d, true, R.drawable.smooth_weather_02n),
        	new WeatherIcon("02n", R.drawable.smooth_weather_02n, false, R.drawable.smooth_weather_02d),
        	new WeatherIcon("03d", R.drawable.smooth_weather_03d, true, R.drawable.smooth_weather_03n),
        	new WeatherIcon("03n", R.drawable.smooth_weather_03n, false, R.drawable.smooth_weather_03d),
        	new WeatherIcon("04d", R.drawable.smooth_weather_04d, true, R.drawable.smooth_weather_04n),
        	new WeatherIcon("04n", R.drawable.smooth_weather_04n, false, R.drawable.smooth_weather_04d),
        	new WeatherIcon("09d", R.drawable.smooth_weather_09d, true, R.drawable.smooth_weather_09n),
        	new WeatherIcon("09n", R.drawable.smooth_weather_09n, false, R.drawable.smooth_weather_09d),
        	new WeatherIcon("10d", R.drawable.smooth_weather_10d, true, R.drawable.smooth_weather_10n),
        	new WeatherIcon("10n", R.drawable.smooth_weather_10n, false, R.drawable.smooth_weather_10d),
        	new WeatherIcon("11d", R.drawable.smooth_weather_11d, true, R.drawable.smooth_weather_11n),
        	new WeatherIcon("11n", R.drawable.smooth_weather_11n, false, R.drawable.smooth_weather_11d),
        	new WeatherIcon("13d", R.drawable.smooth_weather_13d, true, R.drawable.smooth_weather_13n),
        	new WeatherIcon("13n", R.drawable.smooth_weather_13n, false, R.drawable.smooth_weather_13d),
        	new WeatherIcon("50d", R.drawable.smooth_weather_50d, true, R.drawable.smooth_weather_50n),
        	new WeatherIcon("50n", R.drawable.smooth_weather_50n, false, R.drawable.smooth_weather_50d),
        	new WeatherIcon("r", R.drawable.smooth_weather_r, true, R.drawable.smooth_weather_r),
        	new WeatherIcon("r", R.drawable.smooth_weather_r, false, R.drawable.smooth_weather_r),
        	new WeatherIcon("sn50", R.drawable.smooth_weather_sn50, true, R.drawable.smooth_weather_sn50),
        	new WeatherIcon("sn50", R.drawable.smooth_weather_sn50, false, R.drawable.smooth_weather_sn50),
        	new WeatherIcon("t50", R.drawable.smooth_weather_t50, true, R.drawable.smooth_weather_t50),
        	new WeatherIcon("t50", R.drawable.smooth_weather_t50, false, R.drawable.smooth_weather_t50),
        	new WeatherIcon("w50", R.drawable.smooth_weather_w50, true, R.drawable.smooth_weather_w50),
        	new WeatherIcon("w50", R.drawable.smooth_weather_w50, false, R.drawable.smooth_weather_w50)
    	};
    
    public WeatherIcon[] m_ImageArrBubble = 
    	{
        	new WeatherIcon("01d", R.drawable.bubble_weather_01d, true, R.drawable.bubble_weather_01n),
        	new WeatherIcon("01n", R.drawable.bubble_weather_01n, false, R.drawable.bubble_weather_01d),
        	new WeatherIcon("02d", R.drawable.bubble_weather_02d, true, R.drawable.bubble_weather_02n),
        	new WeatherIcon("02n", R.drawable.bubble_weather_02n, false, R.drawable.bubble_weather_02d),
        	new WeatherIcon("03d", R.drawable.bubble_weather_03d, true, R.drawable.bubble_weather_03n),
        	new WeatherIcon("03n", R.drawable.bubble_weather_03n, false, R.drawable.bubble_weather_03d),
        	new WeatherIcon("04d", R.drawable.bubble_weather_04d, true, R.drawable.bubble_weather_04n),
        	new WeatherIcon("04n", R.drawable.bubble_weather_04n, false, R.drawable.bubble_weather_04d),
        	new WeatherIcon("09d", R.drawable.bubble_weather_09d, true, R.drawable.bubble_weather_09n),
        	new WeatherIcon("09n", R.drawable.bubble_weather_09n, false, R.drawable.bubble_weather_09d),
        	new WeatherIcon("10d", R.drawable.bubble_weather_10d, true, R.drawable.bubble_weather_10n),
        	new WeatherIcon("10n", R.drawable.bubble_weather_10n, false, R.drawable.bubble_weather_10d),
        	new WeatherIcon("11d", R.drawable.bubble_weather_11d, true, R.drawable.bubble_weather_11n),
        	new WeatherIcon("11n", R.drawable.bubble_weather_11n, false, R.drawable.bubble_weather_11d),
        	new WeatherIcon("13d", R.drawable.bubble_weather_13d, true, R.drawable.bubble_weather_13n),
        	new WeatherIcon("13n", R.drawable.bubble_weather_13n, false, R.drawable.bubble_weather_13d),
        	new WeatherIcon("50d", R.drawable.bubble_weather_50d, true, R.drawable.bubble_weather_50n),
        	new WeatherIcon("50n", R.drawable.bubble_weather_50n, false, R.drawable.bubble_weather_50d),
        	new WeatherIcon("r", R.drawable.bubble_weather_r, true, R.drawable.bubble_weather_r),
        	new WeatherIcon("r", R.drawable.bubble_weather_r, false, R.drawable.bubble_weather_r),
        	new WeatherIcon("sn50", R.drawable.bubble_weather_sn50, true, R.drawable.bubble_weather_sn50),
        	new WeatherIcon("sn50", R.drawable.bubble_weather_sn50, false, R.drawable.bubble_weather_sn50),
        	new WeatherIcon("t50", R.drawable.bubble_weather_t50, true, R.drawable.bubble_weather_t50),
        	new WeatherIcon("t50", R.drawable.bubble_weather_t50, false, R.drawable.bubble_weather_t50),
        	new WeatherIcon("w50", R.drawable.bubble_weather_w50, true, R.drawable.bubble_weather_w50),
        	new WeatherIcon("w50", R.drawable.bubble_weather_w50, false, R.drawable.bubble_weather_w50)
    	};
    
    public WeatherIcon[] m_ImageArrStylish = 
    	{
        	new WeatherIcon("01d", R.drawable.stylish_weather_01d, true, R.drawable.stylish_weather_01n),
        	new WeatherIcon("01n", R.drawable.stylish_weather_01n, false, R.drawable.stylish_weather_01d),
        	new WeatherIcon("02d", R.drawable.stylish_weather_02d, true, R.drawable.stylish_weather_02n),
        	new WeatherIcon("02n", R.drawable.stylish_weather_02n, false, R.drawable.stylish_weather_02d),
        	new WeatherIcon("03d", R.drawable.stylish_weather_03d, true, R.drawable.stylish_weather_03n),
        	new WeatherIcon("03n", R.drawable.stylish_weather_03n, false, R.drawable.stylish_weather_03d),
        	new WeatherIcon("04d", R.drawable.stylish_weather_04d, true, R.drawable.stylish_weather_04n),
        	new WeatherIcon("04n", R.drawable.stylish_weather_04n, false, R.drawable.stylish_weather_04d),
        	new WeatherIcon("09d", R.drawable.stylish_weather_09d, true, R.drawable.stylish_weather_09n),
        	new WeatherIcon("09n", R.drawable.stylish_weather_09n, false, R.drawable.stylish_weather_09d),
        	new WeatherIcon("10d", R.drawable.stylish_weather_10d, true, R.drawable.stylish_weather_10n),
        	new WeatherIcon("10n", R.drawable.stylish_weather_10n, false, R.drawable.stylish_weather_10d),
        	new WeatherIcon("11d", R.drawable.stylish_weather_11d, true, R.drawable.stylish_weather_11n),
        	new WeatherIcon("11n", R.drawable.stylish_weather_11n, false, R.drawable.stylish_weather_11d),
        	new WeatherIcon("13d", R.drawable.stylish_weather_13d, true, R.drawable.stylish_weather_13n),
        	new WeatherIcon("13n", R.drawable.stylish_weather_13n, false, R.drawable.stylish_weather_13d),
        	new WeatherIcon("50d", R.drawable.stylish_weather_50d, true, R.drawable.stylish_weather_50n),
        	new WeatherIcon("50n", R.drawable.stylish_weather_50n, false, R.drawable.stylish_weather_50d),
        	new WeatherIcon("r", R.drawable.stylish_weather_r, true, R.drawable.stylish_weather_r),
        	new WeatherIcon("r", R.drawable.stylish_weather_r, false, R.drawable.stylish_weather_r),
        	new WeatherIcon("sn50", R.drawable.stylish_weather_sn50, true, R.drawable.stylish_weather_sn50),
        	new WeatherIcon("sn50", R.drawable.stylish_weather_sn50, false, R.drawable.stylish_weather_sn50),
        	new WeatherIcon("t50", R.drawable.stylish_weather_t50, true, R.drawable.stylish_weather_t50),
        	new WeatherIcon("t50", R.drawable.stylish_weather_t50, false, R.drawable.stylish_weather_t50),
        	new WeatherIcon("w50", R.drawable.stylish_weather_w50, true, R.drawable.stylish_weather_w50),
        	new WeatherIcon("w50", R.drawable.stylish_weather_w50, false, R.drawable.stylish_weather_w50)
    	};
    
    public WeatherIcon[] m_ImageArrGarmahis = 
    	{
        	new WeatherIcon("01d", R.drawable.garmahis_weather_01d, true, R.drawable.garmahis_weather_01n),
        	new WeatherIcon("01n", R.drawable.garmahis_weather_01n, false, R.drawable.garmahis_weather_01d),
        	new WeatherIcon("02d", R.drawable.garmahis_weather_02d, true, R.drawable.garmahis_weather_02n),
        	new WeatherIcon("02n", R.drawable.garmahis_weather_02n, false, R.drawable.garmahis_weather_02d),
        	new WeatherIcon("03d", R.drawable.garmahis_weather_03d, true, R.drawable.garmahis_weather_03n),
        	new WeatherIcon("03n", R.drawable.garmahis_weather_03n, false, R.drawable.garmahis_weather_03d),
        	new WeatherIcon("04d", R.drawable.garmahis_weather_04d, true, R.drawable.garmahis_weather_04n),
        	new WeatherIcon("04n", R.drawable.garmahis_weather_04n, false, R.drawable.garmahis_weather_04d),
        	new WeatherIcon("09d", R.drawable.garmahis_weather_09d, true, R.drawable.garmahis_weather_09n),
        	new WeatherIcon("09n", R.drawable.garmahis_weather_09n, false, R.drawable.garmahis_weather_09d),
        	new WeatherIcon("10d", R.drawable.garmahis_weather_10d, true, R.drawable.garmahis_weather_10n),
        	new WeatherIcon("10n", R.drawable.garmahis_weather_10n, false, R.drawable.garmahis_weather_10d),
        	new WeatherIcon("11d", R.drawable.garmahis_weather_11d, true, R.drawable.garmahis_weather_11n),
        	new WeatherIcon("11n", R.drawable.garmahis_weather_11n, false, R.drawable.garmahis_weather_11d),
        	new WeatherIcon("13d", R.drawable.garmahis_weather_13d, true, R.drawable.garmahis_weather_13n),
        	new WeatherIcon("13n", R.drawable.garmahis_weather_13n, false, R.drawable.garmahis_weather_13d),
        	new WeatherIcon("50d", R.drawable.garmahis_weather_50d, true, R.drawable.garmahis_weather_50n),
        	new WeatherIcon("50n", R.drawable.garmahis_weather_50n, false, R.drawable.garmahis_weather_50d),
        	new WeatherIcon("r", R.drawable.garmahis_weather_r, true, R.drawable.garmahis_weather_r),
        	new WeatherIcon("r", R.drawable.garmahis_weather_r, false, R.drawable.garmahis_weather_r),
        	new WeatherIcon("sn50", R.drawable.garmahis_weather_sn50, true, R.drawable.garmahis_weather_sn50),
        	new WeatherIcon("sn50", R.drawable.garmahis_weather_sn50, false, R.drawable.garmahis_weather_sn50),
        	new WeatherIcon("t50", R.drawable.garmahis_weather_t50, true, R.drawable.garmahis_weather_t50),
        	new WeatherIcon("t50", R.drawable.garmahis_weather_t50, false, R.drawable.garmahis_weather_t50),
        	new WeatherIcon("w50", R.drawable.garmahis_weather_w50, true, R.drawable.garmahis_weather_w50),
        	new WeatherIcon("w50", R.drawable.garmahis_weather_w50, false, R.drawable.garmahis_weather_w50)
    	};
    
    public WeatherIcon[] m_ImageArrIconBest = 
    	{
        	new WeatherIcon("01d", R.drawable.iconbest_weather_01d, true, R.drawable.iconbest_weather_01n),
        	new WeatherIcon("01n", R.drawable.iconbest_weather_01n, false, R.drawable.iconbest_weather_01d),
        	new WeatherIcon("02d", R.drawable.iconbest_weather_02d, true, R.drawable.iconbest_weather_02n),
        	new WeatherIcon("02n", R.drawable.iconbest_weather_02n, false, R.drawable.iconbest_weather_02d),
        	new WeatherIcon("03d", R.drawable.iconbest_weather_03d, true, R.drawable.iconbest_weather_03n),
        	new WeatherIcon("03n", R.drawable.iconbest_weather_03n, false, R.drawable.iconbest_weather_03d),
        	new WeatherIcon("04d", R.drawable.iconbest_weather_04d, true, R.drawable.iconbest_weather_04n),
        	new WeatherIcon("04n", R.drawable.iconbest_weather_04n, false, R.drawable.iconbest_weather_04d),
        	new WeatherIcon("09d", R.drawable.iconbest_weather_09d, true, R.drawable.iconbest_weather_09n),
        	new WeatherIcon("09n", R.drawable.iconbest_weather_09n, false, R.drawable.iconbest_weather_09d),
        	new WeatherIcon("10d", R.drawable.iconbest_weather_10d, true, R.drawable.iconbest_weather_10n),
        	new WeatherIcon("10n", R.drawable.iconbest_weather_10n, false, R.drawable.iconbest_weather_10d),
        	new WeatherIcon("11d", R.drawable.iconbest_weather_11d, true, R.drawable.iconbest_weather_11n),
        	new WeatherIcon("11n", R.drawable.iconbest_weather_11n, false, R.drawable.iconbest_weather_11d),
        	new WeatherIcon("13d", R.drawable.iconbest_weather_13d, true, R.drawable.iconbest_weather_13n),
        	new WeatherIcon("13n", R.drawable.iconbest_weather_13n, false, R.drawable.iconbest_weather_13d),
        	new WeatherIcon("50d", R.drawable.iconbest_weather_50d, true, R.drawable.iconbest_weather_50n),
        	new WeatherIcon("50n", R.drawable.iconbest_weather_50n, false, R.drawable.iconbest_weather_50d),
        	new WeatherIcon("r", R.drawable.iconbest_weather_r, true, R.drawable.iconbest_weather_r),
        	new WeatherIcon("r", R.drawable.iconbest_weather_r, false, R.drawable.iconbest_weather_r),
        	new WeatherIcon("sn50", R.drawable.iconbest_weather_sn50, true, R.drawable.iconbest_weather_sn50),
        	new WeatherIcon("sn50", R.drawable.iconbest_weather_sn50, false, R.drawable.iconbest_weather_sn50),
        	new WeatherIcon("t50", R.drawable.iconbest_weather_t50, true, R.drawable.iconbest_weather_t50),
        	new WeatherIcon("t50", R.drawable.iconbest_weather_t50, false, R.drawable.iconbest_weather_t50),
        	new WeatherIcon("w50", R.drawable.iconbest_weather_w50, true, R.drawable.iconbest_weather_w50),
        	new WeatherIcon("w50", R.drawable.iconbest_weather_w50, false, R.drawable.iconbest_weather_w50)
    	};
    
    public WeatherIcon[] m_ImageArrCartoon = 
    	{
        	new WeatherIcon("01d", R.drawable.cartoon_weather_01d, true, R.drawable.cartoon_weather_01n),
        	new WeatherIcon("01n", R.drawable.cartoon_weather_01n, false, R.drawable.cartoon_weather_01d),
        	new WeatherIcon("02d", R.drawable.cartoon_weather_02d, true, R.drawable.cartoon_weather_02n),
        	new WeatherIcon("02n", R.drawable.cartoon_weather_02n, false, R.drawable.cartoon_weather_02d),
        	new WeatherIcon("03d", R.drawable.cartoon_weather_03d, true, R.drawable.cartoon_weather_03n),
        	new WeatherIcon("03n", R.drawable.cartoon_weather_03n, false, R.drawable.cartoon_weather_03d),
        	new WeatherIcon("04d", R.drawable.cartoon_weather_04d, true, R.drawable.cartoon_weather_04n),
        	new WeatherIcon("04n", R.drawable.cartoon_weather_04n, false, R.drawable.cartoon_weather_04d),
        	new WeatherIcon("09d", R.drawable.cartoon_weather_09d, true, R.drawable.cartoon_weather_09n),
        	new WeatherIcon("09n", R.drawable.cartoon_weather_09n, false, R.drawable.cartoon_weather_09d),
        	new WeatherIcon("10d", R.drawable.cartoon_weather_10d, true, R.drawable.cartoon_weather_10n),
        	new WeatherIcon("10n", R.drawable.cartoon_weather_10n, false, R.drawable.cartoon_weather_10d),
        	new WeatherIcon("11d", R.drawable.cartoon_weather_11d, true, R.drawable.cartoon_weather_11n),
        	new WeatherIcon("11n", R.drawable.cartoon_weather_11n, false, R.drawable.cartoon_weather_11d),
        	new WeatherIcon("13d", R.drawable.cartoon_weather_13d, true, R.drawable.cartoon_weather_13n),
        	new WeatherIcon("13n", R.drawable.cartoon_weather_13n, false, R.drawable.cartoon_weather_13d),
        	new WeatherIcon("50d", R.drawable.cartoon_weather_50d, true, R.drawable.cartoon_weather_50n),
        	new WeatherIcon("50n", R.drawable.cartoon_weather_50n, false, R.drawable.cartoon_weather_50d),
        	new WeatherIcon("r", R.drawable.cartoon_weather_r, true, R.drawable.cartoon_weather_r),
        	new WeatherIcon("r", R.drawable.cartoon_weather_r, false, R.drawable.cartoon_weather_r),
        	new WeatherIcon("sn50", R.drawable.cartoon_weather_sn50, true, R.drawable.cartoon_weather_sn50),
        	new WeatherIcon("sn50", R.drawable.cartoon_weather_sn50, false, R.drawable.cartoon_weather_sn50),
        	new WeatherIcon("t50", R.drawable.cartoon_weather_t50, true, R.drawable.cartoon_weather_t50),
        	new WeatherIcon("t50", R.drawable.cartoon_weather_t50, false, R.drawable.cartoon_weather_t50),
        	new WeatherIcon("w50", R.drawable.cartoon_weather_w50, true, R.drawable.cartoon_weather_w50),
        	new WeatherIcon("w50", R.drawable.cartoon_weather_w50, false, R.drawable.cartoon_weather_w50)
    	};
        
}



