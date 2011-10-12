#!/usr/bin/perl -w

###############################################################################
#
# PlNoidStomp.
#
# Mints a NOID id and returns it via a Message Broker.
#
# Apache License 2.0
#
# reverse('©'), ait.co.at
#
###############################################################################

package NoidStomp;

use strict;
use warnings;
use Noid;
use BerkeleyDB;
use Mojolicious::Lite;
use Log::Log4perl qw(get_logger :levels);

my $DBDIR = ".";
my $DBNAME = "$DBDIR/NOID/noid.bdb";

# configure logger
my $logger = get_logger("PlNoidStomp");
my $appender = Log::Log4perl::Appender->new(
    "Log::Dispatch::Screen",
    mode     => "append",
);
my $layout = Log::Log4perl::Layout::PatternLayout->new("%d %p> %F{1}:%L %M - %m%n");
$logger->level($DEBUG); 
$appender->layout($layout);
$logger->add_appender($appender);
$logger->info("starting $0\n");

# NOID settings
my $noid = Noid::dbopen($DBNAME,0);
my $minted_id;

sub mint {
  $minted_id = Noid::mint($noid,"system",0);
  $logger->info("minted $minted_id \n");
  return $minted_id;
}

# Simple route with plain text response
get '/mint' => sub { shift->render(text => mint() ) };

# RESTful web service sending JSON responses
get '/service/:offset' => sub {
  my $self   = shift;
  my $offset = $self->param('offset') || 23;
  $self->render(json => {list => [0 .. $offset]});
};

app->secret('demo1234');

app->start;