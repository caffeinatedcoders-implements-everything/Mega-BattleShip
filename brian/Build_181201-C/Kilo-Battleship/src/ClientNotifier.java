/*
  Runs on it's own thread on the client side and provides
  public, methods for the Client to set the
  currentShot generated by Client processes. Filters duplicate shots
  from client.

  SRP: Loop(Receive and send the current shot from the client to server)

  -Chris
*/

import java.io.ObjectOutputStream;
import java.util.HashSet;

public class ClientNotifier extends Notifier {

  // Current Shot. Updated constantly by containing Client
  private Shot newShot = null; // Object that will be sent to ServerListener

  // Hash structure provides a caching mechanism to filter duplicate Shots from Server
  private HashSet<String> shotCache;

  /**
   * Constructor
   *
   * @param _outputStream From containing Connection
   */
  ClientNotifier(ObjectOutputStream _outputStream) {
    super(_outputStream);
    shotCache = new HashSet<>();
  }

  /**
   * sendObject()
   * Override NotifierInterface interface
   * Sends currentShot to server
   */
  @Override
  public synchronized void sendObject() {
    try {
      outputStream.reset();
      outputStream.writeObject(newShot);
      outputStream.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * setObject()
   * Override NotifierInterface interface
   * Sets currentShot
   *
   * @param _newShot From containing Client
   */
  @Override
  public synchronized void setNewShot(Shot _newShot) {
    newShot = _newShot;
  }

  private synchronized Shot getNewShot() {
    return newShot;
  }

  /**
   * run()
   * Override Runnable interface
   * ClientNotifier thread
   */
  @Override
  public void run() {
    try {
      while (getNewShot() == null) {
        Thread.sleep(50);
      }

      while (true) {
        if (!shotCache.contains(getNewShot().getShotKey())) {
          sendObject();
          shotCache.add(getNewShot().getShotKey());
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
