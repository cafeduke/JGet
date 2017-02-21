
# A wrapper around the system call to check for failures
sub systemWithAbort {
    my $status = system(@_);

    print "*** XYZ ** status=$status, cmd=@_ \n\n";
    
    print "Command @_ failed: exit status is $status\n\n" if $status;
    exit 1 if $status;
    return $status;
}


# Need this for 'require' to work
return 1
