
package com.krishagni.catissueplus.core.notification.schedular;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;

import com.krishagni.catissueplus.core.common.CaTissueAppContext;
import com.krishagni.catissueplus.core.notification.services.ExternalAppNotificationService;

import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.logger.Logger;

public class ExternalAppNotificationSchedular implements Runnable {

	private static final Logger LOGGER = Logger.getCommonLogger(ExternalAppNotificationSchedular.class);

	private static final String SCH_TIME_INTERVAL = "extAppSchTimeIntervalInMinutes";

	public static void scheduleExtAppNotifSchedulerJob() throws Exception {
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(100);
		int schTimeInterval = 60;
		String schTimeIntervalProperty = XMLPropertyHandler.getValue(SCH_TIME_INTERVAL).trim();
		if (!schTimeIntervalProperty.isEmpty() || schTimeIntervalProperty != null)
			schTimeInterval = Integer.parseInt(schTimeIntervalProperty);
		executor.scheduleWithFixedDelay(new ExternalAppNotificationSchedular(), 0, 10, TimeUnit.SECONDS);
	}

	@Override
	public void run() {
		try {
			ApplicationContext caTissueContext = CaTissueAppContext.getInstance();
			ExternalAppNotificationService extApp = (ExternalAppNotificationService) caTissueContext
					.getBean("extAppNotificationService");
			extApp.notifyExternalApps();
		}
		catch (Exception ex) {
			LOGGER.error("Error while Notifiying External Applications :" + ex.getMessage());
		}

	}

}
