package stellarapi;

import java.io.File;
import java.io.IOException;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import stellarapi.api.StellarSkyAPI;
import stellarapi.client.ClientSettings;
import stellarapi.client.DefaultHourProvider;
import stellarapi.client.StellarKeyHook;
import stellarapi.client.StellarSkyClientHandler;
import stellarapi.stellars.Optics;
import stellarapi.stellars.layer.CelestialManager;

public class ClientProxy extends CommonProxy implements IProxy {
	
	private static final String clientConfigCategory = "clientconfig";
	private static final String clientConfigOpticsCategory = "clientconfig.optics";
	
	private ClientSettings clientSettings = new ClientSettings();
	
	private CelestialManager celestialManager = new CelestialManager(true);
	
	public ClientSettings getClientSettings() {
		return this.clientSettings;
	}
	
	@Override
	public CelestialManager getClientCelestialManager() {
		return this.celestialManager;
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {		
        this.setupConfigManager(event.getSuggestedConfigurationFile());
        
		MinecraftForge.EVENT_BUS.register(new StellarSkyClientHandler());
		FMLCommonHandler.instance().bus().register(new StellarKeyHook());
		
		StellarSkyAPI.registerHourProvider(new DefaultHourProvider(this.clientSettings));
	}

	@Override
	public void load(FMLInitializationEvent event) throws IOException {
		super.load(event);
		
    	celestialManager.initializeClient(this.clientSettings);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}
	
	@Override
	public void setupConfigManager(File file) {
		super.setupConfigManager(file);
		cfgManager.register(clientConfigCategory, this.clientSettings);
		cfgManager.register(clientConfigOpticsCategory, Optics.instance);
	}
	
	@Override
	public World getDefWorld() {
		return Minecraft.getMinecraft().theWorld;
	}
	
	@Override
	public World getDefWorld(boolean isRemote) {
		return isRemote? this.getDefWorld() : super.getDefWorld();
	}
}
