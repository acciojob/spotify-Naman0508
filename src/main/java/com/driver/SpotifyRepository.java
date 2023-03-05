package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name , mobile);
        users.add(user);
        userPlaylistMap.put(user , new ArrayList<>());
        return user;
    }
    public Artist checkIfArtistExists(String artistName){
        for(Artist artist : artists){
            if(artistName.equals(artist.getName()))
                return artist;

        }
        return null;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        artistAlbumMap.put(artist , new ArrayList<>());
        return artist;
    }


    public Album createAlbum(String title, String artistName) {

        Artist artist= checkIfArtistExists(artistName);
        if(artist==null){
            artist = createArtist(artistName);
        }
        Album album= new Album(title);
        albums.add(album);
        artistAlbumMap.get(artist).add(album);
        albumSongMap.put(album , new ArrayList<>());
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        for(Album album:albums){
            if(album.getTitle().equals(albumName)) {
                Song song=new Song(title,length);
                songs.add(song);
                List<Song> list=new ArrayList<>();
                if(albumSongMap.containsKey(album)) list=albumSongMap.get(album);
                list.add(song);
                albumSongMap.put(album,list);
                return song;
            }
        }
        throw new Exception("Album does not exist");
    }


    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        Playlist playlist=new Playlist(title);
        playlists.add(playlist);
        List<Song> listOfSongs=new ArrayList<>();
        for(Song song:songs){
            if(song.getLength()==length) listOfSongs.add(song);
        }
        playlistSongMap.put(playlist,listOfSongs);


        User creator=new User();
        for(User user:users){
            if(user.getMobile().equals(mobile)) {
                creatorPlaylistMap.put(user,playlist);

                List<User> listOfUsers=new ArrayList<>();
                if(playlistListenerMap.containsKey(playlist)) listOfUsers=playlistListenerMap.get(playlist);
                listOfUsers.add(user);
                playlistListenerMap.put(playlist,listOfUsers);

                List<Playlist> listOfPlaylists=new ArrayList<>();
                if(userPlaylistMap.containsKey(user)) listOfPlaylists=userPlaylistMap.get(user);
                listOfPlaylists.add(playlist);
                userPlaylistMap.put(user,listOfPlaylists);

                return playlist;
            }
        }
        throw new Exception("User does not exist");

    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        Playlist playlist=new Playlist(title);
        playlists.add(playlist);
        List<Song> listOfSongs=new ArrayList<>();
        for(Song song:songs){
            if(songTitles.contains(song.getTitle())){
                listOfSongs.add(song);
            }
        }
        playlistSongMap.put(playlist,listOfSongs);

        User creator=new User();
        for(User user:users){
            if(user.getMobile().equals(mobile)) {
                creatorPlaylistMap.put(user,playlist);

                List<User> listOfUsers=new ArrayList<>();
                if(playlistListenerMap.containsKey(playlist)) listOfUsers=playlistListenerMap.get(playlist);
                listOfUsers.add(user);
                playlistListenerMap.put(playlist,listOfUsers);

                List<Playlist> listOfPlaylists=new ArrayList<>();
                if(userPlaylistMap.containsKey(user)) listOfPlaylists=userPlaylistMap.get(user);
                listOfPlaylists.add(playlist);
                userPlaylistMap.put(user,listOfPlaylists);

                return playlist;
            }
        }
        throw new Exception("User does not exist");

    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        Playlist currentPlaylist=new Playlist();

        for(Playlist playlist:playlists){
            if(playlist.getTitle().equals((playlistTitle))){
                currentPlaylist=playlist;
            }
        }
        if(currentPlaylist.getTitle()==null) throw new Exception("Playlist does not exist");

        User currentUser=new User();

        for(User user:users){
            if(user.getMobile().equals(mobile)){
                currentUser=user;
            }
        }
        if(currentUser.getName()==null) throw new Exception("User does not exist");

        List<User> listOfUsers=new ArrayList<>();
        if(playlistListenerMap.containsKey(currentPlaylist)){
            listOfUsers=playlistListenerMap.get(currentPlaylist);
        }
        if(!listOfUsers.contains(currentUser)) listOfUsers.add(currentUser);
        playlistListenerMap.put(currentPlaylist,listOfUsers);

        if(!creatorPlaylistMap.containsKey(currentUser)) creatorPlaylistMap.put(currentUser,currentPlaylist);

        List<Playlist> listOfPlaylists=new ArrayList<>();
        if(userPlaylistMap.containsKey(currentUser)) listOfPlaylists=userPlaylistMap.get(currentUser);
        if(!listOfPlaylists.contains(currentPlaylist)) listOfPlaylists.add(currentPlaylist);
        userPlaylistMap.put(currentUser,listOfPlaylists);

        return currentPlaylist;


    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User currentUser=new User();

        for(User user:users){
            if(user.getMobile().equals(mobile)){
                currentUser=user;
            }
        }
        if(currentUser.getName()==null)
            throw new Exception("User does not exist");

        Song currentSong=new Song();

        for(Song song:songs){
            if(song.getTitle().equals(songTitle)){
                currentSong=song;
            }
        }
        if(currentSong.getTitle()==null)
            throw new Exception("Song does not exist");

        List<User> listOfUsers=new ArrayList<>();

        if(songLikeMap.containsKey(currentSong))
            listOfUsers=songLikeMap.get(currentSong);

        if(!listOfUsers.contains(currentUser)) {
            listOfUsers.add(currentUser);
            currentSong.setLikes(currentSong.getLikes() + 1);
            songLikeMap.put(currentSong, listOfUsers);


            for (Album album : albumSongMap.keySet()) {
                List<Song> songList = albumSongMap.get(album);
                if (songList.contains(currentSong)) {
                    for (Artist artist : artistAlbumMap.keySet()) {
                        List<Album> albumList = artistAlbumMap.get(artist);
                        if (albumList.contains(album)) {
                            artist.setLikes(artist.getLikes() + 1);
                            return currentSong;
                        }
                    }
                }
            }
        }
        return currentSong;


    }

    public String mostPopularArtist() {
        String ans="";
        int max=0;
        for(Artist artist:artists){
            if(artist.getLikes()>max){
                max=artist.getLikes();
                ans=artist.getName();
            }
        }
        return ans;
    }

    public String mostPopularSong() {
        String ans="";
        int max=0;
        for(Song song: songs){
            if(song.getLikes()>max){
                max=song.getLikes();
                ans= song.getTitle();
            }
        }
        return ans;
    }

}
