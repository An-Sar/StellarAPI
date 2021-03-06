package stellarapi.lib.gui;

public interface IFontHelper {

	public float getStringWidth(String string);

	public float getStringHeight();

	public String trimStringToWidth(String toTrim, float width);

	public String trimStringToWidth(String toTrim, float width, boolean fromEnd);

}
