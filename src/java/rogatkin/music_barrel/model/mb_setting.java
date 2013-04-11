package rogatkin.music_barrel.model;

import com.beegman.webbee.model.AppModel;
import com.beegman.webbee.util.SimpleCoordinator;
import org.aldan3.annot.DBField;
import org.aldan3.annot.DataRelation;
import org.aldan3.annot.FormField;
import org.aldan3.annot.OptionMap;
import org.aldan3.data.util.DataFiller;
import org.aldan3.data.util.FieldFiller;
import com.beegman.webbee.util.DODelegatorEx;

@DataRelation
public class mb_setting extends SimpleCoordinator<MBModel> {
	public enum output_type {
		ANALOG, HDMI, SPDIF
	};

	public mb_setting(MBModel arg0) {
		super(arg0);
	}

	@DBField()
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
}