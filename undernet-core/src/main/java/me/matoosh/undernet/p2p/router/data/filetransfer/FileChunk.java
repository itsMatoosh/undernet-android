package me.matoosh.undernet.p2p.router.data.filetransfer;

import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.message.MsgBase;

/**
 * Represents a file chunk.
 * Created by Mateusz Rębacz on 14.10.2017.
 */

public class FileChunk implements MsgBase {
    /**
     * The id of the transfer the chunk belongs to.
     */
    public NetworkID chunkId;
    /**
     * The data of this chunk.
     */
    public byte[] data;
}