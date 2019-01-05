package com.perficient.library.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class PriceUtils {

    public static final String CNYUTIL = "CNY";

    public static final String USDUTIL = "USD";

    public static final String REGEX_PRICENUM = "\\d+(\\.\\d*)?";

    public static String getPrice(String price) {
        String priceNum = "";
        String priceUtil = "";
        if (!StringUtils.isBlank(price)) {
            Pattern patternPriceNum = Pattern.compile(REGEX_PRICENUM);
            Matcher matcher = patternPriceNum.matcher(price);
            if (matcher.find()) {
                priceNum = matcher.group();
            }

            if (price.contains("美元") || price.contains("$") || price.contains("USD")) {
                priceUtil = USDUTIL;
            } else {
                priceUtil = CNYUTIL;
            }

        }

        return priceNum + priceUtil;
    }
}
