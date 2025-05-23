package rogatkin.music_barrel.model;

import com.beegman.webbee.model.AppModel;
import com.beegman.webbee.util.SimpleCoordinator;
import org.aldan3.annot.DBField;
import org.aldan3.annot.DataRelation;
import org.aldan3.annot.FormField;
import org.aldan3.annot.FormField.FieldType;
import org.aldan3.annot.OptionMap;
import org.aldan3.data.util.DataFiller;
import org.aldan3.util.DataConv;
import org.aldan3.data.util.FieldFiller;
import com.beegman.webbee.util.DODelegatorEx;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.HashSet;
import java.util.TimeZone;
import org.aldan3.data.util.FieldConverter;

@DataRelation
public class mb_setting extends SimpleCoordinator<MBModel> {
	public enum output_type {
		ANALOG, HDMI, SPDIF, AUTO, USB
	};

	public mb_setting(MBModel arg0) {
		super(arg0);
	}

	@DBField(unique = true)
	public int id;

	@DBField()
	@FormField(presentType=FieldType.Readonly)
	public Date last_scan;

	@DBField()
	@FormField(defaultTo = "40", presentSize = 3)
	public int page_size;

	@DBField(size = 40)
	@FormField(defaultTo = "UTF-8", presentSize = 16, presentFiller = CharsetsFiller.class)
	public String char_encoding;

	@DBField()
	@FormField
	public boolean perform_scan;
	
	@DBField()
	@FormField
	public boolean allow_duplicates;
	
	@DBField(size=1000)
	@FormField
	public String music_dir;
	
	public String last_directory;
	
	public float last_volume;

	@FormField
	public boolean rescan_soon;

	@DBField(size = 30, converter = AOEnumConv.class)
	@FormField(presentFiller = EnumFiller.class)
	public output_type output_type;

	@OptionMap(valueMap = "name"/*, labelMap = "name"*/)
	public static class EnumFiller implements FieldFiller<DODelegatorEx[], mb_setting> {
		public DODelegatorEx[] fill(mb_setting modelObject, String filter) {
			output_type[] enumVals = mb_setting.output_type.class.getEnumConstants();
			DODelegatorEx[] result = new DODelegatorEx[enumVals.length];
			int i = 0;
			for (output_type v : enumVals) {
				result[i++] = new DODelegatorEx<output_type>(v) {
					@Override
					public Object get(String name) {
						//System.err.printf("Called with %s%n", name);
						if ("name".equals(name) || "label".equals(name))
							return principal.name();
						return super.get(name);
					}

					@Override
					public boolean meanFieldFilter(String name) {
						return "name".equals(name);
					}
				};
			}
			return result;
		}
	}

	public static class AOEnumConv implements FieldConverter<output_type> {
		@Override
		public output_type convert(String value, TimeZone tz, Locale l) {
		    try {
			    return Enum.valueOf(output_type.class, value);
		    } catch(java.lang.IllegalArgumentException iae) {
		        System.err.printf("Output type %s can't be converted, AUTO used instead%n", value);
		        return Enum.valueOf(output_type.class, "AUTO");
		    }
		}

		@Override
		public String deConvert(output_type value, TimeZone tz, Locale l) {
			if (value == null)
				return "";
			return value.toString();
		}
	}

	// TODO write utility class array filler 
	public static class CharsetsFiller implements FieldFiller<DODelegatorEx[], mb_setting> {
		public DODelegatorEx[] fill(mb_setting modelObject, String filter) {
			Set<String> charsets = null;
			if ("8".equals(System.getProperty("java.specification.version")))
				charsets = new HashSet<>(Arrays.asList(DataConv.HTML5_CHARSETS));
			else
				charsets = Charset.availableCharsets().keySet();
			DODelegatorEx[] result = new DODelegatorEx[charsets.size()];
			int i = 0;
			for (String is : charsets) {
				result[i++] = new DODelegatorEx<String>(is) {
					@Override
					public Object get(String name) {
						if ("name".equals(name) || "label".equals(name))
							return principal;
						return super.get(name);
					}

					@Override
					public boolean meanFieldFilter(String name) {
						return "name".equals(name);
					}
				};
			}
			return result;
		}
	}
}
