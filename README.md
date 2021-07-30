# Skype-clone


## User exist direct got to home page

```java
  @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() !=null){
            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            finish();
            return;
        }
    }
```

## Firefox Database refernce

```java
databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
```

## RecyclerView Android

```java
public  class customRecyclerView extends RecyclerView.Adapter<customUsersViewHolder>{
        @NonNull

        ArrayList<All_users_list> users_lists;
        Context context;

        customRecyclerView(Context context, ArrayList<All_users_list> users_lists){
            this.context=context;
            this.users_lists=users_lists;
        }

        @Override
        public customUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new customUsersViewHolder(LayoutInflater.from(context).inflate(R.layout.main_activity_design, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MainActivity.customUsersViewHolder holder, int position) {
            String username=users_lists.get(position).getUsername();
            String status=users_lists.get(position).getStatus();
            String imageUrl=users_lists.get(position).getImageUrl();

            holder.username_textView.setText(username);
            ImageLoader.with(MainActivity.this).from(imageUrl).load(holder.dp_imageView);
            holder.userContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(MainActivity.this, VideoCallActivity.class);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return users_lists.size();
        }
    }
```

## Getting image from gallery

```java
public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }

@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_INTENT){
            profileUri=data.getData();
            img_profile.setImageURI(profileUri);
        }
    }
```
