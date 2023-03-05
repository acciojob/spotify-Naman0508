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
    public Album checkIfAlbumExists(String albumName){
        for (Album album: albums){
            if(album.getTitle().equals(albumName))
                return album;

        }
        return null;
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
        Album album= checkIfAlbumExists(albumName);
        if(album==null){
            throw new Exception("Album does not exists");
        }
        Song song = new Song(title , length);
        songs.add(song);
        albumSongMap.get(album).add(song);
        songLikeMap.put(song , new ArrayList<>());
        return song;
    }


    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User user = checkIfUserExists(mobile);
        if(user==null){
            throw new Exception("User does not exists");

        }
        Playlist playlist = new Playlist(title);
        List<Song> listOfSongs= getSongWithGivenLength(length);
        playlistSongMap.put(playlist, listOfSongs);
        playlistListenerMap.put (playlist, new ArrayList<>());
        playlistListenerMap.get(playlist).add(user);
        creatorPlaylistMap.put(user, playlist);
        userPlaylistMap.get(user).add(playlist);
        playlists.add(playlist);
        return playlist;

    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User user = checkIfUserExists(mobile);
        if(user==null){
            throw new Exception("User does not exists");

        }
        Playlist playlist = new Playlist(title);
        List<Song> listOfSongs = new ArrayList<>();
        for(String s: songTitles){
            getSongsWithGivenTitle(s, listOfSongs);
        }
        playlistSongMap.put (playlist, listOfSongs);
        playlistListenerMap.put(playlist, new ArrayList<>());
        playlistListenerMap.get(playlist).add(user);
        creatorPlaylistMap.put(user, playlist);
        userPlaylistMap.get(user).add(playlist);
        playlists.add(playlist);
        return playlist;

    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        Playlist playlist = null;
        User user= null;
        playlist= checkIfPlaylistExists(playlistTitle);
        user= checkIfUserExists(mobile);
        if(playlist==null){
            throw new Exception("Playlist does not exists");

        }
        if(user==null){
            throw new Exception("User does not exists");
        }

        boolean isUserListenerOrCreator= checkIfUserIsCreator(user, playlist) ||checkIfUserIsListener(user, playlist);
        if(isUserListenerOrCreator){
            return playlist;
        }
        playlistListenerMap.get(playlist).add(user);
        userPlaylistMap.get(user).add(playlist);
        return playlist;

    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user= null;
        Song song=null;
        int songLikes=0;
        int artistLikes=0;
        user=checkIfUserExists(mobile);
        song=checkIfSongExists(songTitle);

        if(user== null){
            throw new Exception("User does not exists");

        }
        if(song ==null){
            throw new Exception("Song does not exist");
        }
        Album album =getAlbumOfTheSong(song);
        Artist artist= getArtistOfTheAlbum(album);

        if(songLikeMap.containsKey(song)){
            if(!(songLikeMap.get(song).contains(user))){
                songLikeMap.get(song).add(user);
                songLikes=song.getLikes()+1;
                artistLikes=artist.getLikes()+1;
                song.setLikes(songLikes);
                artist.setLikes(artistLikes);
            }
        }
        else{
            songLikeMap.put(song, new ArrayList<>());
            songLikeMap.get(song).add(user);
            songLikes=song.getLikes()+1;
            artistLikes=artist.getLikes()+1;
            song.setLikes(songLikes);
            artist.setLikes(artistLikes);
        }
        return song;

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
    public User checkIfUserExists(String mobile){
        for(User user: users){
            if(user.getMobile().equals(mobile)){
                return user;

            }
        }
        return null;
    }

    public List<Song> getSongWithGivenLength(int l){
        List<Song> listSongs = new ArrayList<>();
        for(Song song : songs){
            if(song.getLength()==l){
                listSongs.add(song);
            }
        }
        return listSongs;
    }
    public void getSongsWithGivenTitle(String title, List<Song> songList){
        for(Song song: songs){
            if(song.getTitle().equals(title)){
                if(!songList.contains(song)){
                    songList.add(song);
                }
            }
        }
    }
    public Playlist checkIfPlaylistExists(String title){
        for(Playlist playlist : playlists){
            if(playlist.getTitle().equals(title)){
                return playlist;
            }
        }
        return null;
    }
    public boolean checkIfUserIsCreator(User user, Playlist playlist){
        if(creatorPlaylistMap.containsKey(user)){
            return creatorPlaylistMap.get(user).equals(playlist);

        }
        return false;
    }
    public boolean checkIfUserIsListener(User user, Playlist playlist){
        return playlistListenerMap.get(playlist).contains(user);
    }

    public Song checkIfSongExists(String songTitle){
        for(Song song : songs){
            if(song.getTitle().equals(songTitle)) {
                return song;
            }
        }
        return null;
    }
    public Album getAlbumOfTheSong(Song song){
        for(Album album : albumSongMap.keySet()){
            if(albumSongMap.get(album).contains(song)){
                return album;
            }
        }
        return null;
    }
    public Artist getArtistOfTheAlbum(Album album){
        for(Artist artist : artistAlbumMap.keySet()){
            if(artistAlbumMap.get(artist).contains(album)){
                return artist;
            }
        }
        return null;
    }
}
