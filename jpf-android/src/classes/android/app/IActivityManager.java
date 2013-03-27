package android.app;

import android.app.ActivityThread.ApplicationThread;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.android.server.am.ActivityManager;

/**
 * These methods are called from ContextImpl when an Activity/Service has to be stopped/started. As we are not
 * modelling AIDL we can just create small model of the interface class that directs the calls to the
 * ActvityManager class.
 * 
 * @author Heila van der Merwe
 * 
 */
public class IActivityManager {

  ActivityManager am;

  public IActivityManager(ActivityManager am) {
    this.am = am;
  }

  public void attachApplication(ApplicationThread mAppThread) throws RemoteException {
    am.attachApplication(mAppThread);

  }

  /* ****************************** Activity Methods *********************************** */

  public void startActivity(Intent intent, int requestCode) {
    am.performLaunchActivity(intent, requestCode);
  }

  public void finishActivity(IBinder token, int resultCode, Intent resultData) {
    am.performFinishActivity(token, resultCode, resultData);
  }

  public void activityPaused(IBinder token) {
    am.performActivityPaused(token);

  }

  public void activityStopped(IBinder token, Bundle state, Object object, CharSequence description) {
    am.performActivityStopped(token, state, object, description);

  }

  public void activityDestroyed(IBinder token) {
    am.performActivityDestroyed(token);
  }

  public boolean willActivityBeVisible(IBinder token) {
    return am.performWillActivityBeVisible(token);

  }

  /* ****************************** Service Methods *********************************** */

  public synchronized ComponentName startService(Intent service, String resolvedType) {
    return am.performStartService(service, resolvedType);
  }

  public synchronized int stopService(Intent service, String resolvedType) {
    return am.performStopService(service, resolvedType);

  }

  public synchronized int bindService(IBinder token, Intent service, String resolvedType,
                                      ServiceConnection connection, int flags) {
    return am.performBindService(token, service, resolvedType, connection, flags);
  }

  public synchronized void unbindService(ServiceConnection conn) {
    am.performUnbindService(conn);
  }

  public synchronized void unbindFinished(Object token, Intent intent, boolean doRebind)
      throws RemoteException {
    am.performUnbindFinished(token, intent, doRebind);
  }

  public synchronized void serviceDoneExecuting(Object token, int i, int j, int k) throws RemoteException {
    am.performServiceDoneExecuting(token, i, j, k);

  }

  public synchronized void publishService(Object token, Intent intent, IBinder binder) throws RemoteException {
    am.performPublishService(token, intent, binder);

  }

  /**
   * Stop self in service
   * 
   * @param componentName
   * @param mToken
   * @param startId
   * @throws RemoteException
   */
  public void stopServiceToken(ComponentName componentName, IBinder mToken, int startId)
      throws RemoteException {
    am.performStopServiceToken(componentName, mToken, startId);
  }

  /* ****************************** BroadcastReciever Methods *********************************** */

  public Intent registerReceiver(ApplicationThread caller, String callerPackage, IIntentReceiver receiver,
                                 IntentFilter filter, String permission) throws RemoteException {
    return am.performRegisterReceiver(caller, callerPackage, receiver, filter, permission);
  }

  public final int broadcastIntent(ApplicationThread caller, Intent intent, String resolvedType,
                                   IIntentReceiver resultTo, int resultCode, String resultData, Bundle map,
                                   String requiredPermission, boolean serialized, boolean sticky)
      throws RemoteException {
    return am.performBroadcastIntent(caller, intent, resolvedType, resultTo, resultCode, resultData, map,
        requiredPermission, serialized, sticky);
  }

  public void unbroadcastIntent(ApplicationThread applicationThread, Intent intent) throws RemoteException {
    am.performUnbroadcastIntent(applicationThread, intent);
  }

  public void finishReceiver(IBinder who, int resultCode, String resultData, Bundle resultExtras,
                             boolean resultAbort) throws RemoteException {
    am.performFinishReceiver(who, resultCode, resultData, resultExtras, resultAbort);
  }

  public void unregisterReceiver(IIntentReceiver receiver) throws RemoteException {
    am.performUnregisterReceiver(receiver);
  }

}